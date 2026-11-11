import React, { useState, useEffect } from "react";
import Layout from "./Layout";
import { C } from "./theme";
import AuthView from "./AuthView";
import AdminView from "./AdminView";
import PaymentOverlay from "./PaymentOverlay";
import { API_BASE, fmt, genId, StatusBadge, ProfileField, formatLicence, LICENCE_RE, PHONE_RE } from "./shared";
import {
  MapPin,
  Gauge,
  CreditCard,
  Landmark,
  Banknote,
  Building2,
} from "lucide-react";

/* ---------------------------------------------------------------
   BACKEND
------------------------------------------------------------------*/


/* ---------------------------------------------------------------
   TOKENS — dark theme
------------------------------------------------------------------*/

const INSURANCE_TYPES = [
  { type: "NONE", label: "None", desc: "Basic third-party liability. High claim excess applies.", dailyPremium: 0 },
  { type: "BASIC", label: "Basic Protection", desc: "Covers mirror dents, light windshield scratches.", dailyPremium: 45 },
  { type: "STANDARD", label: "Standard Excess Limit", desc: "Excess reduced by 60%. Recommended for city commutes.", dailyPremium: 85 },
  { type: "PREMIUM", label: "Premium Waiver", desc: "Zero deductible / no-excess waiver. Complete peace of mind.", dailyPremium: 150 },
];

const PAYMENT_METHODS = [
  { id: "Card", label: "Credit/Debit Card", icon: CreditCard },
  { id: "EFT", label: "EFT Secure", icon: Landmark },
  { id: "Cash", label: "Cash on Collection", icon: Banknote },
];

const BANKS = ["ABSA", "Capitec", "FNB", "Nedbank", "Standard Bank", "TymeBank", "African Bank"];

const EMPTY_PAY = { cardNumber: "", cardName: "", expiry: "", cvv: "", bank: "", accountHolder: "", accountNumber: "" };
const EMPTY_DRAFT = {
  carId: "",
  startDate: "",
  endDate: "",
  insuranceType: "STANDARD",
  paymentMethod: "Card",
  pay: EMPTY_PAY,
};

function formatCardNumber(v) {
  return v.replace(/\D/g, "").slice(0, 16).replace(/(.{4})/g, "$1 ").trim();
}
function formatExpiry(v) {
  const d = v.replace(/\D/g, "").slice(0, 4);
  return d.length > 2 ? `${d.slice(0, 2)}/${d.slice(2)}` : d;
}
function expiryOk(v) {
  const m = /^(\d{2})\/(\d{2})$/.exec(v);
  if (!m) return false;
  const month = +m[1];
  const year = 2000 + +m[2];
  if (month < 1 || month > 12) return false;
  const now = new Date();
  return year > now.getFullYear() || (year === now.getFullYear() && month >= now.getMonth() + 1);
}
// Returns an object of error messages (empty when valid) for the chosen payment method.
function paymentErrors(method, p) {
  const e = {};
  if (method === "Card") {
    if (p.cardNumber.replace(/\s/g, "").length !== 16) e.cardNumber = "Card number must be 16 digits";
    if (!p.cardName.trim()) e.cardName = "Enter the name on the card";
    if (!expiryOk(p.expiry)) e.expiry = "Use a valid future date (MM/YY)";
    if (!/^\d{3}$/.test(p.cvv)) e.cvv = "CVV must be 3 digits";
  } else if (method === "EFT") {
    if (!p.bank) e.bank = "Choose your bank";
    if (!p.accountHolder.trim()) e.accountHolder = "Enter the account holder name";
    if (!/^\d{9,11}$/.test(p.accountNumber)) e.accountNumber = "Account number must be 9–11 digits";
  }
  return e;
}

// Today as YYYY-MM-DD in the user's local time zone.
function todayStr() {
  return new Date().toLocaleDateString("en-CA");
}
function nextDay(dateStr) {
  const d = new Date(dateStr + "T00:00:00");
  d.setDate(d.getDate() + 1);
  return d.toLocaleDateString("en-CA");
}

function rentalDuration(startDate, endDate) {
  if (!startDate || !endDate) return 0;
  const diff = Math.round((new Date(endDate) - new Date(startDate)) / 86400000);
  return diff > 0 ? diff : 0;
}

function carImage(car) {
  if (car.image) return car.image;
  const slug = `${car.make}-${car.model}`.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
  return `/cars/${slug}.jpg`;
}

function managerImage(name) {
  const slug = (name || "").toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
  return `/managers/${slug}.jpg`;
}

// Shows /managers/<first-last>.jpg; falls back to initials if the photo is missing.
function ManagerAvatar({ name, src }) {
  const [failed, setFailed] = useState(false);
  const initials = (name || "?").split(" ").map((w) => w[0]).slice(0, 2).join("").toUpperCase();
  return (
    <div
      className="rounded-full overflow-hidden flex-shrink-0 flex items-center justify-center text-sm font-bold"
      style={{ width: 40, height: 40, background: C.border, color: C.muted }}
    >
      {failed ? (
        initials
      ) : (
        <img
          src={src || managerImage(name)}
          alt={name}
          className="w-full h-full object-cover"
          onError={() => setFailed(true)}
        />
      )}
    </div>
  );
}



export default function CarRental() {
  const [tab, setTab] = useState("fleet");
  const [bookings, setBookings] = useState([]);
  const [payStage, setPayStage] = useState(null); // null | "processing" | { receipt }
  const [theme, setTheme] = useState(() => {
    try {
      return localStorage.getItem("theme") || "dark";
    } catch {
      return "dark";
    }
  });
  function toggleTheme() {
    const next = theme === "dark" ? "light" : "dark";
    setTheme(next);
    try {
      localStorage.setItem("theme", next);
    } catch {}
  }

  // ----- Data fetched from the Spring Boot backend -----
  const [cars, setCars] = useState([]);
  const [garages, setGarages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(null);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        setLoadError(null);

        const [carsRes, garagesRes] = await Promise.all([
          fetch(`${API_BASE}/car`),
          fetch(`${API_BASE}/garage`),
        ]);

        if (!carsRes.ok) throw new Error(`Failed to load cars (${carsRes.status})`);
        if (!garagesRes.ok) throw new Error(`Failed to load garages (${garagesRes.status})`);

        const carsData = await carsRes.json();
        const garagesData = await garagesRes.json();

        setCars(carsData);
        setGarages(garagesData);
      } catch (err) {
        console.error("Error loading data from backend:", err);
        setLoadError(err.message);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, []);

  // ----- Profile state. No login/password this term — the renter
  // just fills in their details once, and it's reused for every
  // booking after that. Swap this for a real Member record once
  // the backend exposes a /api/members endpoint.
  const [currentUser, setCurrentUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("user") || "null");
    } catch {
      return null;
    }
  });
  const hasProfile = !!currentUser;
  const isAdmin = currentUser?.role === "ADMIN";

  // Admins don't use the customer Fleet page; send them to the Admin page.
  useEffect(() => {
    if (isAdmin && (tab === "fleet" || tab === "login")) setTab("admin");
  }, [isAdmin, tab]);

  function updateUser(u) {
    setCurrentUser(u);
    try {
      if (u) localStorage.setItem("user", JSON.stringify(u));
      else localStorage.removeItem("user");
    } catch {}
  }
  function handleAuth(u) {
    updateUser(u);
    setTab(u.role === "ADMIN" ? "admin" : "fleet");
  }
  function logout() {
    updateUser(null);
    setBookings([]);
    setDraft(EMPTY_DRAFT);
    setTab("fleet");
  }

  // Load this customer's booking history from the backend (survives refresh).
  useEffect(() => {
    if (!currentUser || isAdmin || loading) return;
    let cancelled = false;
    (async () => {
      try {
        const [bRes, pRes] = await Promise.all([fetch(`${API_BASE}/booking`), fetch(`${API_BASE}/payment`)]);
        if (!bRes.ok) throw new Error(`Failed to load bookings (${bRes.status})`);
        const all = await bRes.json();
        const pays = pRes.ok ? await pRes.json() : [];
        const methodOf = {};
        pays.forEach((p) => {
          methodOf[p.bookingId] = p.method;
        });
        const mine = all
          .filter((b) => b.memberId === currentUser.memberId)
          .map((b) => {
            const car = cars.find((c) => c.carId === b.carId);
            return {
              bookingId: b.bookingId,
              car: car ? `${car.make} ${car.model}` : b.carId,
              dates: `${b.startDate} – ${b.endDate}`,
              days: rentalDuration(b.startDate, b.endDate),
              insurance: "—",
              total: b.totalCost,
              status: b.status,
              carId: b.carId,
              memberId: b.memberId,
              startDate: b.startDate,
              endDate: b.endDate,
              paymentMethod: methodOf[b.bookingId] || "Card",
            };
          })
          .reverse();
        if (!cancelled) setBookings(mine);
      } catch (err) {
        console.error("Error loading bookings:", err);
      }
    })();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentUser?.memberId, loading]);

  const [draft, setDraft] = useState(EMPTY_DRAFT);

  const selectedCar = cars.find((c) => c.carId === draft.carId);
  const days = rentalDuration(draft.startDate, draft.endDate);
  const insurance = INSURANCE_TYPES.find((i) => i.type === draft.insuranceType);
  const carCost = selectedCar ? selectedCar.dailyRate * days : 0;
  const insuranceCost = insurance ? insurance.dailyPremium * days : 0;
  const totalCost = carCost + insuranceCost;
  const payErrors = paymentErrors(draft.paymentMethod, draft.pay);
  const canSubmit =
    hasProfile && draft.carId && days > 0 && draft.startDate >= todayStr() && Object.keys(payErrors).length === 0;

  function startBookingFor(carId) {
    if (!currentUser || isAdmin) {
      setTab("login");
      return;
    }
    setDraft((d) => ({ ...d, carId }));
    setTab("book");
  }

  // Saves a new status for a car in the backend and in the local list.
  async function setCarStatus(carId, status) {
    const car = cars.find((c) => c.carId === carId);
    if (!car) return;
    try {
      const res = await fetch(`${API_BASE}/car`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...car, status }),
      });
      if (!res.ok) throw new Error(`Failed to update car (${res.status})`);
      setCars((list) => list.map((c) => (c.carId === carId ? { ...c, status } : c)));
    } catch (err) {
      console.error("Error updating car status:", err);
    }
  }

  async function submitBooking(e) {
    e.preventDefault();
    if (!canSubmit) return;

    const method = draft.paymentMethod;
    const needsProcessing = method !== "Cash";
    const paymentId = genId("PAY");
    const pay = draft.pay;
    // Only the last 4 digits are ever shown on the receipt; full details are never stored or sent.
    const methodDetail =
      method === "Card"
        ? `Card ending ${pay.cardNumber.replace(/\D/g, "").slice(-4)}`
        : method === "EFT"
        ? `${pay.bank} EFT (acc. ending ${pay.accountNumber.slice(-4)})`
        : "Cash on collection";

    if (needsProcessing) setPayStage("processing");
    const minDelay = new Promise((r) => setTimeout(r, needsProcessing ? 2500 : 0));

    try {
      const bookingId = genId("BK");
      const res = await fetch(`${API_BASE}/booking`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          bookingId,
          carId: selectedCar.carId,
          memberId: currentUser.memberId,
          startDate: draft.startDate,
          endDate: draft.endDate,
          totalCost: totalCost,
          status: "CONFIRMED",
        }),
      });
      if (!res.ok) throw new Error(`Failed to create booking (${res.status})`);
      const saved = await res.json();

      const paymentRes = await fetch(`${API_BASE}/payment`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          paymentId,
          bookingId: saved.bookingId,
          amount: totalCost,
          paymentDate: new Date().toISOString().slice(0, 10),
          method,
        }),
      });
      if (!paymentRes.ok) throw new Error(`Failed to record payment (${paymentRes.status})`);

      await setCarStatus(selectedCar.carId, "BOOKED");

      setBookings((b) => [
        {
          bookingId: saved.bookingId,
          car: `${selectedCar.make} ${selectedCar.model}`,
          dates: `${draft.startDate} – ${draft.endDate}`,
          days,
          insurance: `${insurance.label.split(" ")[0]} (R${insurance.dailyPremium}/day)`,
          total: totalCost,
          status: saved.status,
          // raw values kept so the booking can be extended later
          carId: saved.carId,
          memberId: saved.memberId,
          startDate: saved.startDate,
          endDate: saved.endDate,
          paymentMethod: method,
        },
        ...b,
      ]);

      const receipt = {
        bookingId: saved.bookingId,
        paymentId,
        date: new Date().toLocaleString("en-ZA", { dateStyle: "medium", timeStyle: "short" }),
        customer: currentUser.name,
        car: `${selectedCar.make} ${selectedCar.model}`,
        dates: `${draft.startDate} – ${draft.endDate}`,
        days,
        carCost,
        insuranceCost,
        insuranceLabel: insurance.label,
        total: totalCost,
        method,
        methodDetail,
      };
      await minDelay; // keep the "processing" screen up for a moment
      setDraft(EMPTY_DRAFT);
      setPayStage({ receipt });
    } catch (err) {
      console.error("Error creating booking:", err);
      setPayStage(null);
      alert("Something went wrong and your booking was not completed. Please try again.");
    }
  }

  async function cancelBooking(bookingId) {
    if (!window.confirm("Cancel this booking?")) return;
    try {
      const res = await fetch(`${API_BASE}/booking/${bookingId}/cancel`, { method: "PUT" });
      if (!res.ok) throw new Error(`Failed to cancel booking (${res.status})`);
      const updated = await res.json();
      const cancelled = bookings.find((b) => b.bookingId === bookingId);
      if (cancelled) await setCarStatus(cancelled.carId, "AVAILABLE");
      setBookings((list) =>
        list.map((b) => (b.bookingId === bookingId ? { ...b, status: updated.status } : b))
      );
    } catch (err) {
      console.error("Error cancelling booking:", err);
      alert("Could not cancel the booking. Please try again.");
    }
  }

  async function extendBooking(b) {
    const input = window.prompt("How many extra days do you want to add?", "1");
    if (input === null) return;
    const extra = parseInt(input, 10);
    if (!Number.isInteger(extra) || extra < 1) {
      alert("Please enter a whole number of days (1 or more).");
      return;
    }
    const d = new Date(b.endDate + "T00:00:00Z");
    d.setUTCDate(d.getUTCDate() + extra);
    const newEndDate = d.toISOString().slice(0, 10);
    const perDay = b.days > 0 ? Math.round(b.total / b.days) : 0;
    if (!perDay) {
      alert("Could not work out the daily price for this booking.");
      return;
    }
    const extraCost = perDay * extra;
    const newTotal = b.total + extraCost;
    if (!window.confirm(`Extend to ${newEndDate}? Extra cost: R${fmt(extraCost)}`)) return;
    try {
      const res = await fetch(`${API_BASE}/booking`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          bookingId: b.bookingId,
          carId: b.carId,
          memberId: b.memberId,
          startDate: b.startDate,
          endDate: newEndDate,
          totalCost: newTotal,
          status: "CONFIRMED",
        }),
      });
      if (!res.ok) throw new Error(`Failed to extend booking (${res.status})`);
      const updated = await res.json();

      const payRes = await fetch(`${API_BASE}/payment`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          paymentId: genId("PAY"),
          bookingId: b.bookingId,
          amount: extraCost,
          paymentDate: new Date().toISOString().slice(0, 10),
          method: b.paymentMethod,
        }),
      });
      if (!payRes.ok) throw new Error(`Failed to record payment (${payRes.status})`);

      setBookings((list) =>
        list.map((x) =>
          x.bookingId === b.bookingId
            ? {
                ...x,
                endDate: updated.endDate,
                dates: `${x.startDate} – ${updated.endDate}`,
                days: x.days + extra,
                total: updated.totalCost,
              }
            : x
        )
      );
    } catch (err) {
      console.error("Error extending booking:", err);
      alert("Could not extend the booking. Please try again.");
    }
  }

  async function handleSaveProfile(form) {
    try {
      const res = await fetch(`${API_BASE}/member/${currentUser.memberId}/profile`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: form.name,
          licenseNumber: form.licenseNumber,
          phoneNumber: form.phoneNumber,
          region: form.region.trim(),
        }),
      });
      if (!res.ok) throw new Error(`Failed to save profile (${res.status})`);
      updateUser(await res.json());
    } catch (err) {
      console.error("Error saving profile:", err);
      alert("Could not save your details. Please try again.");
    }
  }

  const nav = !currentUser
    ? [{ id: "fleet", label: "Fleet" }, { id: "garages", label: "Garages" }, { id: "login", label: "Login" }]
    : isAdmin
    ? [{ id: "admin", label: "Admin" }, { id: "garages", label: "Garages" }]
    : [{ id: "fleet", label: "Fleet" }, { id: "garages", label: "Garages" }, { id: "book", label: "New Booking" }, { id: "account", label: "My Account" }];

  return (
    <Layout nav={nav} tab={tab} onTabChange={setTab} theme={theme} onToggleTheme={toggleTheme} user={currentUser} onLogout={logout}>
        {loading && (
          <p className="text-sm mb-6" style={{ color: C.muted }}>
            Loading fleet from server…
          </p>
        )}
        {loadError && (
          <p className="text-sm mb-6" style={{ color: C.red }}>
            Couldn't reach the backend ({loadError}). Make sure Spring Boot is running on {API_BASE}.
          </p>
        )}

        {tab === "fleet" && <FleetView cars={cars} garages={garages} onBook={startBookingFor} />}
        {tab === "garages" && <GaragesView garages={garages} />}
        <PaymentOverlay
          stage={payStage}
          onDone={() => {
            setPayStage(null);
            setTab("account");
          }}
        />
        {tab === "login" && <AuthView onAuth={handleAuth} />}
        {tab === "book" &&
          (currentUser && !isAdmin ? (
            <BookingView
              user={currentUser}
              cars={cars}
              draft={draft}
              setDraft={setDraft}
              selectedCar={selectedCar}
              days={days}
              insurance={insurance}
              carCost={carCost}
              insuranceCost={insuranceCost}
              totalCost={totalCost}
              canSubmit={canSubmit}
              onSubmit={submitBooking}
            />
          ) : (
            <AuthView onAuth={handleAuth} />
          ))}
        {tab === "account" &&
          (currentUser && !isAdmin ? (
            <AccountView user={currentUser} bookings={bookings} onSaveProfile={handleSaveProfile} onCancel={cancelBooking} onExtend={extendBooking} />
          ) : (
            <AuthView onAuth={handleAuth} />
          ))}
        {tab === "admin" && isAdmin && <AdminView cars={cars} setCars={setCars} />}
    </Layout>
  );
}

/* ---------------- FLEET ---------------- */
function FleetView({ cars, garages, onBook }) {
  return (
    <section>
      <h1 className="text-3xl font-extrabold mb-1">Our Fleet</h1>
      <p className="text-sm mb-8" style={{ color: C.muted }}>
        Browse available vehicles in Cape Town and find your next ride
      </p>

      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {cars.map((car) => {
          // Cars have no garageId in the backend yet, so they fall back to the first garage.
          const garage = garages.find((g) => g.garageId === car.garageId) || garages[0];
          const available = car.status === "AVAILABLE";
          return (
            <div key={car.carId} className="tm-card rounded-xl overflow-hidden" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
              <div className="relative" style={{ aspectRatio: "16/10", background: C.panelAlt }}>
                <img
                  src={carImage(car)}
                  alt={`${car.make} ${car.model}`}
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    e.currentTarget.onerror = null;
                    e.currentTarget.src = "/cars/placeholder.svg";
                  }}
                />
                <div className="absolute top-3 right-3">
                  <StatusBadge status={car.status} />
                </div>
              </div>
              <div className="p-4">
                <h3 className="font-bold text-base mb-1">
                  {car.make} {car.model}
                </h3>
                <p className="text-xs mb-3" style={{ color: C.faint }}>
                  {car.year}
                </p>
                <div className="flex items-center gap-4 text-xs mb-1" style={{ color: C.muted }}>
                  <span className="flex items-center gap-1">
                    <Gauge size={13} /> {fmt(car.mileage)} km
                  </span>
                </div>
                <p className="flex items-center gap-1 text-xs mb-4" style={{ color: C.muted }}>
                  <MapPin size={13} /> {garage?.garageName}
                </p>
                <div className="flex items-end justify-between">
                  <div>
                    <span className="text-xl font-extrabold" style={{ color: C.amber }}>
                      R{fmt(car.dailyRate)}
                    </span>
                    <p className="text-xs" style={{ color: C.faint }}>
                      per day
                    </p>
                  </div>
                  <button
                    disabled={!available}
                    onClick={() => onBook(car.carId)}
                    className="tm-btn text-xs font-bold px-4 py-2.5 rounded-lg"
                    style={{ background: C.amber, color: C.onAmber }}
                  >
                    {available ? "BOOK THIS CAR" : "NOT AVAILABLE"}
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

/* ---------------- GARAGES ---------------- */
function GaragesView({ garages }) {
  return (
    <section>
      <h1 className="text-3xl font-extrabold mb-1">Our Garages</h1>
      <p className="text-sm mb-8" style={{ color: C.muted }}>
        Pickup and drop-off locations around Cape Town
      </p>

      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {garages.map((g) => {
                    return (
            <div key={g.garageId} className="rounded-xl p-5" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
              <div className="flex items-start justify-between mb-2">
                <h3 className="font-bold text-base">{g.garageName}</h3>
                <MapPin size={16} style={{ color: C.amber }} />
              </div>
              <p className="text-xs mb-1" style={{ color: C.muted }}>
                {g.address}
              </p>
              <p className="text-xs flex items-center gap-1 mb-4" style={{ color: C.muted }}>
                <Building2 size={12} /> {g.city}
              </p>

              <div className="flex items-center gap-3 p-3 rounded-lg" style={{ background: C.panelAlt, border: `1px solid ${C.border}` }}>
                <ManagerAvatar name={g.managerName} src={g.avatar} />
                <div>
                  <p className="text-sm font-semibold">{g.managerName}</p>
                  <p className="text-xs" style={{ color: C.faint }}>
                    Garage Manager
                  </p>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

/* ---------------- PROFILE FORM (one-time, no login/password) ---------------- */

function ProfileForm({ initial, onSave }) {
  const [form, setForm] = useState({
    name: initial?.name || "",
    licenseNumber: initial?.licenseNumber || "",
    phoneNumber: initial?.phoneNumber || "",
    region: initial?.region || "",
  });
  const set = (k) => (v) => setForm((f) => ({ ...f, [k]: v }));
  const licenceOk = LICENCE_RE.test(form.licenseNumber);
  const phoneOk = PHONE_RE.test(form.phoneNumber);
  const canSubmit = form.name.trim() && licenceOk && phoneOk && form.region.trim();

  function submit(e) {
    e.preventDefault();
    if (!canSubmit) return;
    onSave(form);
  }

  return (
    <section className="max-w-md mx-auto">
      <h1 className="text-3xl font-extrabold mb-1 text-center">Your Details</h1>
      <p className="text-sm mb-8 text-center" style={{ color: C.muted }}>
        Fill this in once — it's reused for every booking, no account needed.
      </p>

      <form onSubmit={submit} className="rounded-xl p-6 flex flex-col gap-4" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
        <ProfileField label="Full Name" value={form.name} onChange={set("name")} placeholder="Your name" />
        <ProfileField
          label="Driver's Licence Number"
          value={form.licenseNumber}
          onChange={(v) => set("licenseNumber")(formatLicence(v))}
          placeholder="CPT-1234"
          maxLength={8}
          error={form.licenseNumber && !licenceOk ? "Must be CPT- followed by 4 digits, e.g. CPT-1234" : ""}
        />
        <ProfileField
          label="Phone Number"
          value={form.phoneNumber}
          onChange={(v) => set("phoneNumber")(v.replace(/\D/g, "").slice(0, 10))}
          placeholder="0812345678"
          inputMode="numeric"
          maxLength={10}
          error={form.phoneNumber && !phoneOk ? "Must be exactly 10 digits" : ""}
        />
        <ProfileField label="Region" value={form.region} onChange={set("region")} placeholder="e.g. Cape Town, South Africa" />
        <button type="submit" disabled={!canSubmit} className="tm-btn w-full py-3 rounded-lg text-sm font-bold mt-2" style={{ background: C.amber, color: C.onAmber }}>
          SAVE DETAILS
        </button>
      </form>
    </section>
  );
}


/* ---------------- NEW BOOKING ---------------- */
function SectionHeader({ number, title }) {
  return (
    <div className="flex items-center gap-3 mb-5">
      <span className="flex items-center justify-center rounded-full text-xs font-bold" style={{ width: 24, height: 24, border: `1px solid ${C.amber}`, color: C.amber }}>
        {number}
      </span>
      <h3 className="font-bold text-base">{title}</h3>
    </div>
  );
}

function Field({ label, value, onChange, type = "text", min }) {
  return (
    <label className="flex flex-col gap-1.5">
      <span className="text-xs" style={{ color: C.muted }}>
        {label}
      </span>
      <input
        type={type}
        value={value}
        min={min}
        onChange={(e) => onChange(e.target.value)}
        className="tm-input text-sm rounded-lg px-3 py-2.5"
        style={{ background: C.panelAlt, border: `1px solid ${C.border}`, color: C.text }}
      />
    </label>
  );
}

// Pop-up notification (modal with an OK button).
function NoticePopup({ message, onClose }) {
  if (!message) return null;
  return (
    <div className="fixed inset-0 flex items-center justify-center px-4" style={{ background: "rgba(0,0,0,0.6)", zIndex: 60 }} onClick={onClose}>
      <div className="rounded-xl p-6 text-center" style={{ background: C.panel, border: `1px solid ${C.border}`, width: 380, maxWidth: "100%" }} onClick={(e) => e.stopPropagation()}>
        <div className="mx-auto mb-3 flex items-center justify-center rounded-full text-xl font-extrabold" style={{ width: 48, height: 48, background: C.amberTint, border: `1px solid ${C.amber}`, color: C.amber }}>
          !
        </div>
        <p className="font-bold text-lg mb-2">Check your dates</p>
        <p className="text-sm mb-5" style={{ color: C.muted }}>
          {message}
        </p>
        <button autoFocus onClick={onClose} className="tm-btn w-full py-3 rounded-lg text-sm font-bold" style={{ background: C.amber, color: C.onAmber }}>
          OK
        </button>
      </div>
    </div>
  );
}

function BookingView({ user, cars, draft, setDraft, selectedCar, days, insurance, carCost, insuranceCost, totalCost, canSubmit, onSubmit }) {
  const [dateNotice, setDateNotice] = useState("");

  function changeStart(v) {
    if (v && v < todayStr()) {
      setDateNotice("The start date can't be in the past. Please choose today or a later date.");
      setDraft((d) => ({ ...d, startDate: "" }));
      return;
    }
    if (v && draft.endDate && draft.endDate <= v) {
      setDateNotice(
        draft.endDate === v
          ? "The start and end dates are the same. A rental must be at least 1 day, so please choose an end date after the start date."
          : "The end date is before the new start date, so it has been cleared. Please choose a new end date."
      );
      setDraft((d) => ({ ...d, startDate: v, endDate: "" }));
      return;
    }
    setDraft((d) => ({ ...d, startDate: v }));
  }

  function changeEnd(v) {
    if (v && draft.startDate && v <= draft.startDate) {
      setDateNotice(
        v === draft.startDate
          ? "The start and end dates are the same. A rental must be at least 1 day, so please choose an end date after the start date."
          : "The end date can't be before the start date. Please choose a later end date."
      );
      setDraft((d) => ({ ...d, endDate: "" }));
      return;
    }
    if (v && v < todayStr()) {
      setDateNotice("The end date can't be in the past. Please choose a later date.");
      setDraft((d) => ({ ...d, endDate: "" }));
      return;
    }
    setDraft((d) => ({ ...d, endDate: v }));
  }

  const setPay = (k, v) => setDraft((d) => ({ ...d, pay: { ...d.pay, [k]: v } }));
  const payErrors = paymentErrors(draft.paymentMethod, draft.pay);
  return (
    <section>
      <h1 className="text-3xl font-extrabold mb-1">New Booking</h1>
      <p className="text-sm mb-8" style={{ color: C.muted }}>
        Complete the reservation below
      </p>

      <NoticePopup message={dateNotice} onClose={() => setDateNotice("")} />

      <form onSubmit={onSubmit} className="grid lg:grid-cols-3 gap-6 items-start">
        <div className="lg:col-span-2 flex flex-col gap-5">
          {/* Renter details now come straight from the logged-in account —
              no re-entry, matching what was registered. */}
          <div className="rounded-xl p-5" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
            <SectionHeader number={1} title="Renter" />
            <div className="grid sm:grid-cols-3 gap-4 text-sm">
              <div>
                <p className="text-xs mb-1" style={{ color: C.faint }}>
                  Name
                </p>
                <p className="font-semibold">{user.name}</p>
              </div>
              <div>
                <p className="text-xs mb-1" style={{ color: C.faint }}>
                  Licence
                </p>
                <p className="font-semibold">{user.licenseNumber}</p>
              </div>
              <div>
                <p className="text-xs mb-1" style={{ color: C.faint }}>
                  Phone
                </p>
                <p className="font-semibold">{user.phoneNumber || "—"}</p>
              </div>
            </div>
          </div>

          <div className="rounded-xl p-5" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
            <SectionHeader number={2} title="Trip Details" />
            <div className="grid sm:grid-cols-3 gap-4">
              <label className="flex flex-col gap-1.5">
                <span className="text-xs" style={{ color: C.muted }}>
                  Select Vehicle
                </span>
                <select
                  value={draft.carId}
                  onChange={(e) => setDraft((d) => ({ ...d, carId: e.target.value }))}
                  className="tm-input text-sm rounded-lg px-3 py-2.5"
                  style={{ background: C.panelAlt, border: `1px solid ${C.border}`, color: C.text }}
                >
                  <option value="">Choose a car</option>
                  {cars.filter((c) => c.status === "AVAILABLE").map((c) => (
                    <option key={c.carId} value={c.carId}>
                      {c.make} {c.model} (R{c.dailyRate}/day)
                    </option>
                  ))}
                </select>
              </label>
              <Field label="Start Date" type="date" min={todayStr()} value={draft.startDate} onChange={changeStart} />
              <Field label="End Date" type="date" min={draft.startDate ? nextDay(draft.startDate) : todayStr()} value={draft.endDate} onChange={changeEnd} />
            </div>
          </div>

          <div className="rounded-xl p-5" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
            <SectionHeader number={3} title="Damage Cover & Insurance" />
            <div className="flex flex-col gap-2">
              {INSURANCE_TYPES.map((opt) => {
                const selected = draft.insuranceType === opt.type;
                return (
                  <label
                    key={opt.type}
                    className="flex items-center justify-between px-4 py-3 rounded-lg cursor-pointer"
                    style={{ background: selected ? C.amber : C.panelAlt, border: `1px solid ${selected ? C.amber : C.border}` }}
                  >
                    <span className="flex items-center gap-3">
                      <input type="radio" name="insurance" checked={selected} onChange={() => setDraft((d) => ({ ...d, insuranceType: opt.type }))} />
                      <span>
                        <span className="block text-sm font-semibold" style={{ color: selected ? C.onAmber : C.text }}>
                          {opt.label}
                        </span>
                        <span className="block text-xs" style={{ color: selected ? C.amberDark : C.faint }}>
                          {opt.desc}
                        </span>
                      </span>
                    </span>
                    <span className="text-sm font-bold" style={{ color: selected ? C.onAmber : C.muted }}>
                      R{opt.dailyPremium}/day
                    </span>
                  </label>
                );
              })}
            </div>
          </div>

          <div className="rounded-xl p-5" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
            <SectionHeader number={4} title="Payment Method" />
            <div className="grid grid-cols-3 gap-3">
              {PAYMENT_METHODS.map((m) => {
                const Icon = m.icon;
                const selected = draft.paymentMethod === m.id;
                return (
                  <button
                    type="button"
                    key={m.id}
                    onClick={() => setDraft((d) => ({ ...d, paymentMethod: m.id }))}
                    className="tm-btn flex flex-col items-center gap-2 py-4 rounded-lg text-xs font-semibold"
                    style={{ background: selected ? C.amberTint : C.panelAlt, border: `1px solid ${selected ? C.amber : C.border}`, color: selected ? C.amber : C.muted }}
                  >
                    <Icon size={18} />
                    {m.label}
                  </button>
                );
              })}
            </div>

            {draft.paymentMethod === "Card" && (
              <div className="grid sm:grid-cols-2 gap-4 mt-5">
                <div className="sm:col-span-2">
                  <ProfileField label="Card Number" value={draft.pay.cardNumber} inputMode="numeric" maxLength={19} placeholder="1234 5678 9012 3456"
                    onChange={(v) => setPay("cardNumber", formatCardNumber(v))} error={draft.pay.cardNumber && payErrors.cardNumber} />
                </div>
                <div className="sm:col-span-2">
                  <ProfileField label="Name on Card" value={draft.pay.cardName} placeholder="As shown on the card"
                    onChange={(v) => setPay("cardName", v)} />
                </div>
                <ProfileField label="Expiry (MM/YY)" value={draft.pay.expiry} inputMode="numeric" maxLength={5} placeholder="08/28"
                  onChange={(v) => setPay("expiry", formatExpiry(v))} error={draft.pay.expiry.length === 5 && payErrors.expiry} />
                <ProfileField label="CVV" value={draft.pay.cvv} inputMode="numeric" maxLength={3} placeholder="123"
                  onChange={(v) => setPay("cvv", v.replace(/\D/g, "").slice(0, 3))} error={draft.pay.cvv && payErrors.cvv} />
              </div>
            )}

            {draft.paymentMethod === "EFT" && (
              <div className="grid sm:grid-cols-2 gap-4 mt-5">
                <label className="flex flex-col gap-1.5 sm:col-span-2">
                  <span className="text-xs" style={{ color: C.muted }}>Bank</span>
                  <select
                    value={draft.pay.bank}
                    onChange={(e) => setPay("bank", e.target.value)}
                    className="tm-input text-sm rounded-lg px-3 py-2.5"
                    style={{ background: C.panelAlt, border: `1px solid ${C.border}`, color: C.text }}
                  >
                    <option value="">Choose your bank</option>
                    {BANKS.map((b) => (
                      <option key={b} value={b}>{b}</option>
                    ))}
                  </select>
                </label>
                <ProfileField label="Account Holder" value={draft.pay.accountHolder} placeholder="Name on the account"
                  onChange={(v) => setPay("accountHolder", v)} />
                <ProfileField label="Account Number" value={draft.pay.accountNumber} inputMode="numeric" maxLength={11} placeholder="9–11 digits"
                  onChange={(v) => setPay("accountNumber", v.replace(/\D/g, "").slice(0, 11))} error={draft.pay.accountNumber && payErrors.accountNumber} />
              </div>
            )}

            {draft.paymentMethod === "Cash" && (
              <p className="text-xs mt-5" style={{ color: C.muted }}>
                Pay in cash when you collect the car. No details needed now.
              </p>
            )}
          </div>
        </div>

        {/* Summary */}
        <div className="rounded-xl p-5 sticky top-6" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
          <p className="text-xs uppercase mb-3" style={{ color: C.faint, letterSpacing: "0.06em" }}>
            Booking Summary
          </p>

          {selectedCar ? (
            <>
              <p className="text-xs mb-1" style={{ color: C.faint }}>
                Selected Vehicle
              </p>
              <p className="font-bold mb-1">
                {selectedCar.make} {selectedCar.model} ({selectedCar.year})
              </p>
              <p className="text-xs mb-4" style={{ color: C.amber }}>
                R{selectedCar.dailyRate} / day
              </p>
            </>
          ) : (
            <p className="text-sm mb-4" style={{ color: C.faint }}>
              No vehicle selected yet
            </p>
          )}

          <SummaryRow label="Duration" value={`${days} days`} />
          <SummaryRow label="Base Car Rental" value={`R${fmt(carCost)}`} />
          <SummaryRow label={insurance ? `${insurance.label} Protection` : "Protection"} value={`R${fmt(insuranceCost)}`} />

          <div style={{ borderTop: `1px solid ${C.border}` }} className="my-4" />

          <div className="flex items-end justify-between mb-5">
            <span className="text-sm" style={{ color: C.muted }}>
              Total Cost
            </span>
            <span className="text-2xl font-extrabold" style={{ color: C.amber }}>
              R{fmt(totalCost)}
            </span>
          </div>

          <button type="submit" disabled={!canSubmit} className="tm-btn w-full py-3 rounded-lg text-sm font-bold" style={{ background: C.amber, color: C.onAmber }}>
            CONFIRM BOOKING
          </button>
        </div>
      </form>
    </section>
  );
}

function SummaryRow({ label, value }) {
  return (
    <div className="flex items-center justify-between py-1.5 text-sm">
      <span style={{ color: C.muted }}>{label}</span>
      <span className="font-semibold">{value}</span>
    </div>
  );
}

/* ---------------- MY ACCOUNT ---------------- */
function AccountView({ user, bookings, onSaveProfile, onCancel, onExtend }) {
  const [editing, setEditing] = useState(false);

  if (editing) {
    return (
      <ProfileForm
        initial={user}
        onSave={(form) => {
          onSaveProfile(form);
          setEditing(false);
        }}
      />
    );
  }

  return (
    <section>
      <h1 className="text-3xl font-extrabold mb-1">My Account</h1>
      <p className="text-sm mb-8" style={{ color: C.muted }}>
        Manage your driver profile and track historical rentals
      </p>

      <div className="rounded-xl p-5 mb-8 flex items-center justify-between flex-wrap gap-4" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
        <div>
          <p className="font-bold text-lg">{user.name}</p>
          <p className="tm-mono text-xs mb-2" style={{ color: C.faint }}>
            ID: {user.memberId}
          </p>
          <span className="text-xs font-semibold px-2.5 py-1 rounded" style={{ background: C.greenBg, border: `1px solid ${C.greenBorder}`, color: C.green }}>
            {user.accountStatus}
          </span>
        </div>
        <div className="flex gap-8 text-sm flex-wrap">
          <div>
            <p className="text-xs mb-1" style={{ color: C.faint }}>
              LICENCE NUMBER
            </p>
            <p className="font-semibold">{user.licenseNumber}</p>
          </div>
          <div>
            <p className="text-xs mb-1" style={{ color: C.faint }}>
              PHONE NUMBER
            </p>
            <p className="font-semibold">{user.phoneNumber || "—"}</p>
          </div>
          <div>
            <p className="text-xs mb-1" style={{ color: C.faint }}>
              REGION
            </p>
            <p className="font-semibold">{user.region}</p>
          </div>
        </div>
        <button
          onClick={() => setEditing(true)}
          className="tm-btn text-xs font-semibold px-4 py-2 rounded-lg"
          style={{ border: `1px solid ${C.border}`, color: C.text }}
        >
          Edit Details
        </button>
      </div>

      <h2 className="font-bold text-lg mb-4">Booking History</h2>
      {bookings.length === 0 ? (
        <p className="text-sm" style={{ color: C.faint }}>
          No bookings yet — head to the fleet to reserve a car.
        </p>
      ) : (
        <div className="rounded-xl overflow-hidden" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
          <table className="w-full text-sm">
            <thead>
              <tr style={{ borderBottom: `1px solid ${C.border}` }}>
                {["ID", "CAR", "DATES", "DAYS", "INSURANCE", "TOTAL", "STATUS", ""].map((h) => (
                  <th key={h} className="text-left px-5 py-3 text-xs font-semibold" style={{ color: C.faint }}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {bookings.map((b) => (
                <tr key={b.bookingId} style={{ borderBottom: `1px solid ${C.border}` }}>
                  <td className="px-5 py-3 font-semibold" style={{ color: C.amber }}>
                    {b.bookingId}
                  </td>
                  <td className="px-5 py-3">{b.car}</td>
                  <td className="px-5 py-3" style={{ color: C.muted }}>
                    {b.dates}
                  </td>
                  <td className="px-5 py-3" style={{ color: C.muted }}>
                    {b.days} days
                  </td>
                  <td className="px-5 py-3" style={{ color: C.muted }}>
                    {b.insurance}
                  </td>
                  <td className="px-5 py-3 font-semibold">R{fmt(b.total)}</td>
                  <td className="px-5 py-3">
                    <StatusBadge status={b.status} />
                  </td>
                  <td className="px-5 py-3">
                    {String(b.status).toUpperCase() === "CONFIRMED" && (
                      <button
                        onClick={() => onExtend(b)}
                        className="tm-btn text-xs font-semibold px-3 py-1.5 rounded-lg mr-2"
                        style={{ border: `1px solid ${C.border}`, color: C.text }}
                      >
                        Extend
                      </button>
                    )}
                    {String(b.status).toUpperCase() === "CONFIRMED" && (
                      <button
                        onClick={() => onCancel(b.bookingId)}
                        className="tm-btn text-xs font-semibold px-3 py-1.5 rounded-lg"
                        style={{ border: `1px solid ${C.redBorder}`, color: C.red }}
                      >
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}