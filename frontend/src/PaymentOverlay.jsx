import React from "react";
import { Check } from "lucide-react";
import { C } from "./theme";
import { fmt } from "./shared";

const CSS = `
  @keyframes tm-spin { to { transform: rotate(360deg); } }
  @media print {
    body * { visibility: hidden; }
    #tm-receipt, #tm-receipt * { visibility: visible; }
    #tm-receipt { position: absolute; left: 0; top: 0; width: 100%; box-shadow: none !important; }
    .tm-no-print { display: none !important; }
  }
`;

function Row({ label, value, bold }) {
  return (
    <div className="flex items-start justify-between gap-6 py-1.5 text-sm">
      <span style={{ color: C.muted }}>{label}</span>
      <span className={bold ? "font-bold text-right" : "font-semibold text-right"}>{value}</span>
    </div>
  );
}

/**
 * stage: null | "processing" | { receipt }
 * Shows a "processing payment" screen, then a proof of payment the customer can print or save as PDF.
 */
export default function PaymentOverlay({ stage, onDone }) {
  if (!stage) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center px-4" style={{ background: "rgba(0,0,0,0.6)", zIndex: 50, overflowY: "auto" }}>
      <style>{CSS}</style>

      {stage === "processing" ? (
        <div className="rounded-xl p-8 text-center" style={{ background: C.panel, border: `1px solid ${C.border}`, width: 340 }}>
          <div
            className="mx-auto mb-5 rounded-full"
            style={{ width: 52, height: 52, border: `4px solid ${C.border}`, borderTopColor: C.amber, animation: "tm-spin 0.9s linear infinite" }}
          />
          <p className="font-bold text-lg mb-1">Processing payment…</p>
          <p className="text-sm" style={{ color: C.muted }}>
            Please don't close or refresh this page.
          </p>
        </div>
      ) : (
        <Receipt r={stage.receipt} onDone={onDone} />
      )}
    </div>
  );
}

function Receipt({ r, onDone }) {
  const paid = r.method !== "Cash";
  return (
    <div id="tm-receipt" className="rounded-xl p-6 my-8" style={{ background: C.panel, border: `1px solid ${C.border}`, width: 440, maxWidth: "100%" }}>
      <div className="text-center mb-5">
        <div className="mx-auto mb-3 flex items-center justify-center rounded-full" style={{ width: 52, height: 52, background: C.greenBg, border: `1px solid ${C.greenBorder}` }}>
          <Check size={26} style={{ color: C.green }} />
        </div>
        <p className="font-extrabold text-xl">{paid ? "Payment successful" : "Booking confirmed"}</p>
        <p className="text-xs uppercase mt-1" style={{ color: C.faint, letterSpacing: "0.06em" }}>
          {paid ? "Proof of payment" : "Proof of booking"}
        </p>
      </div>

      <div style={{ borderTop: `1px solid ${C.border}`, borderBottom: `1px solid ${C.border}` }} className="py-3">
        <Row label="Booking ID" value={r.bookingId} />
        <Row label="Payment reference" value={r.paymentId} />
        <Row label="Date" value={r.date} />
        <Row label="Customer" value={r.customer} />
      </div>

      <div style={{ borderBottom: `1px solid ${C.border}` }} className="py-3">
        <Row label="Vehicle" value={r.car} />
        <Row label="Rental period" value={r.dates} />
        <Row label="Duration" value={`${r.days} day${r.days === 1 ? "" : "s"}`} />
        <Row label="Car rental" value={`R${fmt(r.carCost)}`} />
        <Row label={`Insurance (${r.insuranceLabel})`} value={`R${fmt(r.insuranceCost)}`} />
      </div>

      <div className="py-3">
        <Row label="Payment method" value={r.methodDetail} />
        <div className="flex items-end justify-between pt-2">
          <span className="text-sm" style={{ color: C.muted }}>
            {paid ? "Total paid" : "Total due on collection"}
          </span>
          <span className="text-2xl font-extrabold" style={{ color: C.amber }}>
            R{fmt(r.total)}
          </span>
        </div>
      </div>

      <div className="tm-no-print flex gap-3 mt-4">
        <button onClick={() => window.print()} className="tm-btn flex-1 py-3 rounded-lg text-sm font-semibold" style={{ border: `1px solid ${C.border}`, color: C.text }}>
          Print / Save PDF
        </button>
        <button onClick={onDone} className="tm-btn flex-1 py-3 rounded-lg text-sm font-bold" style={{ background: C.amber, color: C.onAmber }}>
          DONE
        </button>
      </div>
    </div>
  );
}