import { C } from "./theme";

/* ---------------------------------------------------------------
   Shared helpers used by App.jsx, AuthView.jsx and AdminView.jsx
------------------------------------------------------------------*/
export const API_BASE = "http://localhost:3060";

export function fmt(n) {
  return (n ?? 0).toLocaleString("en-ZA");
}

export function genId(prefix) {
  return `${prefix}-${Math.random().toString(36).slice(2, 6).toUpperCase()}`;
}

export function StatusBadge({ status, admin = false }) {
  const map = {
    AVAILABLE: { bg: C.greenBg, border: C.greenBorder, text: C.green, label: "AVAILABLE" },
    BOOKED: { bg: C.redBg, border: C.redBorder, text: C.red, label: "NOT AVAILABLE" },
    UNDER_MAINTENANCE: { bg: C.redBg, border: C.redBorder, text: C.red, label: "UNDER MAINTENANCE" },
    COMPLETED: { bg: C.greenBg, border: C.greenBorder, text: C.green, label: "COMPLETED" },
    CANCELLED: { bg: C.redBg, border: C.redBorder, text: C.red, label: "CANCELLED" },
    CONFIRMED: { bg: C.greenBg, border: C.greenBorder, text: C.green, label: "CONFIRMED" },
  };
  const s = map[String(status).toUpperCase()] || map.AVAILABLE;
  // The admin sees "BOOKED"; customers see "NOT AVAILABLE".
  const label = admin && String(status).toUpperCase() === "BOOKED" ? "BOOKED" : s.label;
  return (
    <span className="text-xs font-semibold px-2.5 py-1 rounded" style={{ background: s.bg, border: `1px solid ${s.border}`, color: s.text, letterSpacing: "0.03em" }}>
      {label}
    </span>
  );
}

export function ProfileField({ label, value, onChange, placeholder, type = "text", maxLength, inputMode, error }) {
  return (
    <label className="flex flex-col gap-1.5">
      <span className="text-xs" style={{ color: C.muted }}>
        {label}
      </span>
      <input
        type={type}
        value={value}
        placeholder={placeholder}
        maxLength={maxLength}
        inputMode={inputMode}
        onChange={(e) => onChange(e.target.value)}
        className="tm-input text-sm rounded-lg px-3 py-2.5"
        style={{ background: C.panelAlt, border: `1px solid ${error ? C.red : C.border}`, color: C.text }}
      />
      {error && (
        <span className="text-xs" style={{ color: C.red }}>
          {error}
        </span>
      )}
    </label>
  );
}

// Licence: "CPT-" followed by exactly 4 digits (e.g. CPT-1234).
export function formatLicence(v) {
  const raw = v.toUpperCase();
  const digits = raw.replace(/^CPT-?/, "").replace(/\D/g, "").slice(0, 4);
  if (digits) return "CPT-" + digits;
  if (raw === "") return "";
  return "CPT-".startsWith(raw) ? raw : "CPT-";
}
export const LICENCE_RE = /^CPT-\d{4}$/;
export const PHONE_RE = /^\d{10}$/;
export const EMAIL_RE = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

/* ---------------- Login token helpers ---------------- */
export function getToken() {
  try {
    return localStorage.getItem("token");
  } catch {
    return null;
  }
}

// Like fetch, but sends the login token. A 401 while logged in means the login has expired.
export async function apiFetch(url, options = {}) {
  const token = getToken();
  const headers = { ...(options.headers || {}) };
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(url, { ...options, headers });
  if (res.status === 401 && token) window.dispatchEvent(new Event("auth-expired"));
  if (res.status === 403 && token) {
    // The admin blocked this account while it was logged in.
    try {
      const text = await res.clone().text();
      if (text.includes("blocked")) window.dispatchEvent(new Event("auth-blocked"));
    } catch {
      /* ignore */
    }
  }
  return res;
}

// Reads the plain-text message the backend sends with an error, or falls back.
export async function errorText(res, fallback) {
  try {
    const t = await res.text();
    return t && t.length < 200 ? t : fallback;
  } catch {
    return fallback;
  }
}

/* ---------------- Insurance options (keep prices in step with INSURANCE_PREMIUMS in BookingService.java) ---------------- */
export const INSURANCE_TYPES = [
  { type: "NONE", label: "None", desc: "Basic third-party liability. High claim excess applies.", dailyPremium: 0 },
  { type: "BASIC", label: "Basic Protection", desc: "Covers mirror dents, light windshield scratches.", dailyPremium: 45 },
  { type: "STANDARD", label: "Standard Excess Limit", desc: "Excess reduced by 60%. Recommended for city commutes.", dailyPremium: 85 },
  { type: "PREMIUM", label: "Premium Waiver", desc: "Zero deductible / no-excess waiver. Complete peace of mind.", dailyPremium: 150 },
];

// "Basic (R45/day)". Bookings made before insurance was saved show a dash.
export function insuranceText(type) {
  const t = INSURANCE_TYPES.find((i) => i.type === String(type || "").toUpperCase());
  return t ? `${t.label.split(" ")[0]} (R${t.dailyPremium}/day)` : "—";
}

/* ---------------- Car photos ---------------- */
function carSlugUrl(car) {
  const slug = `${car.make}-${car.model}`.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
  return `/cars/${slug}.jpg`;
}

// Order tried: photo uploaded by the admin, then /cars/<make-model>.jpg in the frontend, then the placeholder.
// versions = { carId: savedTime } from GET /car/images
export function carImage(car, versions) {
  if (car.image) return car.image;
  if (versions && versions[car.carId]) return `${API_BASE}/uploads/cars/${car.carId}.jpg?v=${versions[car.carId]}`;
  return carSlugUrl(car);
}

export function carImgError(e, car) {
  const img = e.currentTarget;
  const slug = carSlugUrl(car);
  if (!img.src.endsWith(slug)) {
    img.src = slug;
  } else {
    img.onerror = null;
    img.src = "/cars/placeholder.svg";
  }
}

/* ---------------- CSV download (opens in Excel) ---------------- */
function csvCell(v) {
  if (v === null || v === undefined) return "";
  if (typeof v === "number") return String(v);
  let s = String(v);
  if (/^\d{6,}$/.test(s)) {
    s = `="${s.replace(/"/g, '""')}"`; // keeps leading zeros (phone numbers) in Excel
  } else if (/^[=+\-@\t\r]/.test(s)) {
    s = "'" + s; // stops a name like =SUM(...) from running as a formula
  }
  return `"${s.replace(/"/g, '""')}"`;
}

export function downloadCsv(filename, headers, rows) {
  const lines = [headers, ...rows].map((r) => r.map(csvCell).join(","));
  const blob = new Blob(["\uFEFF" + lines.join("\r\n")], { type: "text/csv;charset=utf-8" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

// Is this car already taken for any part of start..end? (the end day is free, so back-to-back bookings are fine)
export function findConflict(busy, carId, start, end) {
  if (!carId || !start || !end) return null;
  return busy.find((b) => b.carId === carId && start < b.endDate && end > b.startDate) || null;
}