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