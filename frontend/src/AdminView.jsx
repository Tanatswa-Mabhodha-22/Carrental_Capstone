import React, { useState, useEffect, useCallback } from "react";
import { C } from "./theme";
import { API_BASE, fmt, genId, StatusBadge, ProfileField } from "./shared";

const CAR_STATUSES = ["AVAILABLE", "BOOKED", "UNDER_MAINTENANCE"];
const EMPTY_CAR = { carId: "", make: "", model: "", year: "", dailyRate: "", mileage: "", status: "AVAILABLE" };
const JSON_HEADERS = { "Content-Type": "application/json" };

const th = "text-left px-5 py-3 text-xs font-semibold";
const td = "px-5 py-3";

export default function AdminView({ cars, setCars }) {
  const [tab, setTab] = useState("bookings");
  const [bookings, setBookings] = useState([]);
  const [members, setMembers] = useState([]);
  const [error, setError] = useState("");
  const [carForm, setCarForm] = useState(null); // null = closed

  const load = useCallback(async () => {
    try {
      setError("");
      const [bRes, mRes] = await Promise.all([fetch(`${API_BASE}/booking`), fetch(`${API_BASE}/member`)]);
      if (!bRes.ok || !mRes.ok) throw new Error("Could not load admin data");
      setBookings(await bRes.json());
      setMembers(await mRes.json());
    } catch (err) {
      setError(err.message);
    }
  }, []);
  useEffect(() => {
    load();
  }, [load]);

  const customers = members.filter((m) => m.role !== "ADMIN");
  const nameOf = (id) => members.find((m) => m.memberId === id)?.name || id;
  const carName = (id) => {
    const c = cars.find((x) => x.carId === id);
    return c ? `${c.make} ${c.model}` : id;
  };

  async function cancelBooking(b) {
    if (!window.confirm(`Cancel booking ${b.bookingId}?`)) return;
    try {
      const res = await fetch(`${API_BASE}/booking/${b.bookingId}/cancel`, { method: "PUT" });
      if (!res.ok) throw new Error();
      const car = cars.find((c) => c.carId === b.carId);
      if (car) {
        const r = await fetch(`${API_BASE}/car`, { method: "PUT", headers: JSON_HEADERS, body: JSON.stringify({ ...car, status: "AVAILABLE" }) });
        if (r.ok) setCars((list) => list.map((c) => (c.carId === car.carId ? { ...c, status: "AVAILABLE" } : c)));
      }
      await load();
    } catch {
      alert("Could not cancel the booking.");
    }
  }

  // ----- cars -----
  const carFormErrors = (f) => {
    const e = {};
    const year = Number(f.year);
    if (!f.make.trim()) e.make = "Required";
    if (!f.model.trim()) e.model = "Required";
    if (!Number.isInteger(year) || year < 1980 || year > new Date().getFullYear() + 1) e.year = "Enter a valid year";
    if (!(Number(f.dailyRate) > 0)) e.dailyRate = "Must be more than 0";
    if (f.mileage === "" || !Number.isInteger(Number(f.mileage)) || Number(f.mileage) < 0) e.mileage = "Whole number, 0 or more";
    return e;
  };

  async function saveCar(e) {
    e.preventDefault();
    if (Object.keys(carFormErrors(carForm)).length) return;
    const isNew = !carForm.carId;
    const body = {
      carId: isNew ? genId("CAR") : carForm.carId,
      make: carForm.make.trim(),
      model: carForm.model.trim(),
      year: Number(carForm.year),
      dailyRate: Number(carForm.dailyRate),
      mileage: Number(carForm.mileage),
      status: carForm.status,
    };
    try {
      const res = await fetch(`${API_BASE}/car`, { method: isNew ? "POST" : "PUT", headers: JSON_HEADERS, body: JSON.stringify(body) });
      if (!res.ok) throw new Error();
      const saved = await res.json();
      setCars((list) => (isNew ? [...list, saved] : list.map((c) => (c.carId === saved.carId ? saved : c))));
      setCarForm(null);
    } catch {
      alert("Could not save the car. Is the backend running?");
    }
  }

  async function deleteCar(c) {
    if (!window.confirm(`Remove ${c.make} ${c.model} from the fleet?`)) return;
    try {
      const res = await fetch(`${API_BASE}/car/${c.carId}`, { method: "DELETE" });
      if (!res.ok) throw new Error();
      setCars((list) => list.filter((x) => x.carId !== c.carId));
    } catch {
      alert("Could not remove the car. It may be linked to existing bookings.");
    }
  }

  const tabs = [
    { id: "bookings", label: `Bookings (${bookings.length})` },
    { id: "customers", label: `Customers (${customers.length})` },
    { id: "cars", label: `Cars (${cars.length})` },
  ];
  const errs = carForm ? carFormErrors(carForm) : {};
  const setF = (k) => (v) => setCarForm((f) => ({ ...f, [k]: v }));

  return (
    <section>
      <h1 className="text-3xl font-extrabold mb-1">Admin</h1>
      <p className="text-sm mb-6" style={{ color: C.muted }}>
        See every booking and customer, and manage the fleet
      </p>

      <div className="flex gap-2 mb-6 flex-wrap">
        {tabs.map((t) => (
          <button
            key={t.id}
            onClick={() => setTab(t.id)}
            className="tm-btn text-sm font-semibold px-4 py-2 rounded-lg"
            style={{
              background: tab === t.id ? C.amber : C.panel,
              color: tab === t.id ? C.onAmber : C.muted,
              border: `1px solid ${tab === t.id ? C.amber : C.border}`,
            }}
          >
            {t.label}
          </button>
        ))}
      </div>

      {error && (
        <p className="text-sm mb-4" style={{ color: C.red }}>
          {error}
        </p>
      )}

      {tab === "bookings" && (
        <Table heads={["ID", "CUSTOMER", "CAR", "DATES", "TOTAL", "STATUS", ""]} empty={bookings.length === 0 ? "No bookings yet." : ""}>
          {[...bookings].reverse().map((b) => (
            <tr key={b.bookingId} style={{ borderBottom: `1px solid ${C.border}` }}>
              <td className={td + " font-semibold"} style={{ color: C.amber }}>{b.bookingId}</td>
              <td className={td}>{nameOf(b.memberId)}</td>
              <td className={td}>{carName(b.carId)}</td>
              <td className={td} style={{ color: C.muted }}>{b.startDate} – {b.endDate}</td>
              <td className={td + " font-semibold"}>R{fmt(b.totalCost)}</td>
              <td className={td}><StatusBadge status={b.status} /></td>
              <td className={td}>
                {String(b.status).toUpperCase() === "CONFIRMED" && (
                  <button onClick={() => cancelBooking(b)} className="tm-btn text-xs font-semibold px-3 py-1.5 rounded-lg" style={{ border: `1px solid ${C.redBorder}`, color: C.red }}>
                    Cancel
                  </button>
                )}
              </td>
            </tr>
          ))}
        </Table>
      )}

      {tab === "customers" && (
        <Table heads={["ID", "NAME", "EMAIL", "LICENCE", "PHONE", "REGION"]} empty={customers.length === 0 ? "No customers yet." : ""}>
          {customers.map((m) => (
            <tr key={m.memberId} style={{ borderBottom: `1px solid ${C.border}` }}>
              <td className={td + " tm-mono"} style={{ color: C.faint }}>{m.memberId}</td>
              <td className={td + " font-semibold"}>{m.name}</td>
              <td className={td} style={{ color: C.muted }}>{m.email || "—"}</td>
              <td className={td}>{m.licenseNumber}</td>
              <td className={td}>{m.phoneNumber}</td>
              <td className={td} style={{ color: C.muted }}>{m.region || "—"}</td>
            </tr>
          ))}
        </Table>
      )}

      {tab === "cars" && (
        <>
          {carForm ? (
            <form onSubmit={saveCar} className="rounded-xl p-5 mb-6 grid sm:grid-cols-3 gap-4" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
              <h3 className="font-bold sm:col-span-3">{carForm.carId ? "Edit car" : "Add a car"}</h3>
              <ProfileField label="Make" value={carForm.make} onChange={setF("make")} placeholder="Toyota" error={carForm.make === "" ? "" : errs.make} />
              <ProfileField label="Model" value={carForm.model} onChange={setF("model")} placeholder="Corolla" error={carForm.model === "" ? "" : errs.model} />
              <ProfileField label="Year" value={carForm.year} onChange={(v) => setF("year")(v.replace(/\D/g, "").slice(0, 4))} inputMode="numeric" placeholder="2024" error={carForm.year ? errs.year : ""} />
              <ProfileField label="Daily rate (R)" value={carForm.dailyRate} onChange={(v) => setF("dailyRate")(v.replace(/[^\d.]/g, ""))} inputMode="decimal" placeholder="680" error={carForm.dailyRate ? errs.dailyRate : ""} />
              <ProfileField label="Mileage (km)" value={carForm.mileage} onChange={(v) => setF("mileage")(v.replace(/\D/g, ""))} inputMode="numeric" placeholder="18906" error={carForm.mileage ? errs.mileage : ""} />
              <label className="flex flex-col gap-1.5">
                <span className="text-xs" style={{ color: C.muted }}>Status</span>
                <select
                  value={carForm.status}
                  onChange={(e) => setF("status")(e.target.value)}
                  className="tm-input text-sm rounded-lg px-3 py-2.5"
                  style={{ background: C.panelAlt, border: `1px solid ${C.border}`, color: C.text }}
                >
                  {CAR_STATUSES.map((s) => (
                    <option key={s} value={s}>{s.replace("_", " ")}</option>
                  ))}
                </select>
              </label>
              <div className="sm:col-span-3 flex gap-3">
                <button type="submit" disabled={Object.keys(errs).length > 0} className="tm-btn text-sm font-bold px-5 py-2.5 rounded-lg" style={{ background: C.amber, color: C.onAmber }}>
                  SAVE CAR
                </button>
                <button type="button" onClick={() => setCarForm(null)} className="tm-btn text-sm font-semibold px-5 py-2.5 rounded-lg" style={{ border: `1px solid ${C.border}`, color: C.text }}>
                  Cancel
                </button>
              </div>
            </form>
          ) : (
            <button onClick={() => setCarForm(EMPTY_CAR)} className="tm-btn text-sm font-bold px-5 py-2.5 rounded-lg mb-6" style={{ background: C.amber, color: C.onAmber }}>
              + ADD CAR
            </button>
          )}

          <div className="flex gap-3 mb-4 flex-wrap text-sm">
            {[
              ["AVAILABLE", "Available"],
              ["BOOKED", "Booked"],
              ["UNDER_MAINTENANCE", "Under maintenance"],
            ].map(([key, label]) => (
              <span key={key} className="flex items-center gap-2 px-3 py-1.5 rounded-lg" style={{ background: C.panel, border: `1px solid ${C.border}`, color: C.muted }}>
                {label}
                <strong style={{ color: C.text }}>{cars.filter((c) => c.status === key).length}</strong>
              </span>
            ))}
          </div>

          <Table heads={["CAR", "YEAR", "DAILY RATE", "MILEAGE", "STATUS", ""]} empty={cars.length === 0 ? "No cars yet." : ""}>
            {cars.map((c) => (
              <tr key={c.carId} style={{ borderBottom: `1px solid ${C.border}` }}>
                <td className={td + " font-semibold"}>{c.make} {c.model}</td>
                <td className={td} style={{ color: C.muted }}>{c.year}</td>
                <td className={td}>R{fmt(c.dailyRate)}</td>
                <td className={td} style={{ color: C.muted }}>{fmt(c.mileage)} km</td>
                <td className={td}><StatusBadge status={c.status} admin /></td>
                <td className={td}>
                  <button
                    onClick={() => setCarForm({ ...c, year: String(c.year), dailyRate: String(c.dailyRate), mileage: String(c.mileage) })}
                    className="tm-btn text-xs font-semibold px-3 py-1.5 rounded-lg mr-2"
                    style={{ border: `1px solid ${C.border}`, color: C.text }}
                  >
                    Edit
                  </button>
                  <button onClick={() => deleteCar(c)} className="tm-btn text-xs font-semibold px-3 py-1.5 rounded-lg" style={{ border: `1px solid ${C.redBorder}`, color: C.red }}>
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </Table>
        </>
      )}
    </section>
  );
}

function Table({ heads, empty, children }) {
  if (empty) {
    return (
      <p className="text-sm" style={{ color: C.faint }}>
        {empty}
      </p>
    );
  }
  return (
    <div className="rounded-xl overflow-x-auto" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
      <table className="w-full text-sm">
        <thead>
          <tr style={{ borderBottom: `1px solid ${C.border}` }}>
            {heads.map((h, i) => (
              <th key={i} className={th} style={{ color: C.faint }}>
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>{children}</tbody>
      </table>
    </div>
  );
}