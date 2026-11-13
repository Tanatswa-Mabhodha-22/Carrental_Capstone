import React, { useState } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { C } from "./theme";
import { findConflict } from "./shared";

const pad = (n) => String(n).padStart(2, "0");
const ymd = (y, m, d) => `${y}-${pad(m + 1)}-${pad(d)}`;
const WEEKDAYS = ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"];

/**
 * Two-month calendar for the New Booking page.
 * Days the chosen car is already booked are greyed out and can't be picked.
 * The day a booking ends is free again, so back-to-back bookings work.
 * Click a start day, then an end day. Clicking again starts over.
 */
export default function AvailabilityCalendar({ carId, busy, startDate, endDate, onPick }) {
  const today = new Date().toLocaleDateString("en-CA");
  const base = startDate ? new Date(startDate + "T00:00:00") : new Date();
  const [view, setView] = useState({ y: base.getFullYear(), m: base.getMonth() });

  const next = (v) => {
    const d = new Date(v.y, v.m + 1, 1);
    return { y: d.getFullYear(), m: d.getMonth() };
  };
  const shift = (delta) =>
    setView((v) => {
      const d = new Date(v.y, v.m + delta, 1);
      return { y: d.getFullYear(), m: d.getMonth() };
    });
  const onCurrentMonth = view.y === Number(today.slice(0, 4)) && view.m === Number(today.slice(5, 7)) - 1;

  const mode = startDate && !endDate ? "end" : "start";
  const booked = (d) => !!carId && busy.some((b) => b.carId === carId && b.startDate <= d && d < b.endDate);
  const canStart = (d) => d >= today && !booked(d);
  const canEnd = (d) => !!startDate && d > startDate && !findConflict(busy, carId, startDate, d);
  const selectable = (d) => (mode === "end" ? (d > startDate ? canEnd(d) : canStart(d)) : canStart(d));

  function click(d) {
    if (!selectable(d)) return;
    if (mode === "end" && d > startDate) onPick(startDate, d);
    else onPick(d, "");
  }

  function Month({ y, m, showPrev, showNext }) {
    const title = new Date(y, m, 1).toLocaleDateString("en-ZA", { month: "long", year: "numeric" });
    const offset = (new Date(y, m, 1).getDay() + 6) % 7; // week starts on Monday
    const daysInMonth = new Date(y, m + 1, 0).getDate();
    const cells = [...Array(offset).fill(null), ...Array.from({ length: daysInMonth }, (_, i) => i + 1)];

    return (
      <div>
        <div className="flex items-center justify-between mb-2" style={{ minHeight: 28 }}>
          {showPrev ? (
            <button type="button" onClick={() => shift(-1)} disabled={onCurrentMonth} aria-label="Previous month" className="tm-btn p-1 rounded" style={{ color: C.muted }}>
              <ChevronLeft size={18} />
            </button>
          ) : (
            <span style={{ width: 26 }} />
          )}
          <p className="text-sm font-semibold">{title}</p>
          {showNext ? (
            <button type="button" onClick={() => shift(1)} aria-label="Next month" className="tm-btn p-1 rounded" style={{ color: C.muted }}>
              <ChevronRight size={18} />
            </button>
          ) : (
            <span style={{ width: 26 }} />
          )}
        </div>

        <div className="grid grid-cols-7 gap-1 text-center">
          {WEEKDAYS.map((w) => (
            <span key={w} className="text-xs py-1" style={{ color: C.faint }}>
              {w}
            </span>
          ))}
          {cells.map((day, i) => {
            if (day === null) return <span key={`e${i}`} />;
            const d = ymd(y, m, day);
            const isBooked = booked(d);
            const isPast = d < today;
            const isStart = d === startDate;
            const isEnd = d === endDate;
            const inRange = startDate && endDate && d > startDate && d < endDate;
            const ok = selectable(d);

            let style = { color: C.text, background: "transparent", border: "1px solid transparent" };
            if (isPast) style = { ...style, color: C.faint, opacity: 0.45 };
            else if (isBooked) style = { ...style, color: C.faint, background: C.border, textDecoration: "line-through" };
            else if (!ok) style = { ...style, color: C.faint, opacity: 0.6 };
            if (inRange) style = { ...style, background: C.amberTint, color: C.amber };
            if (isStart || isEnd) style = { ...style, background: C.amber, color: C.onAmber, textDecoration: "none", opacity: 1, fontWeight: 700 };
            if (d === today && !isStart && !isEnd) style = { ...style, border: `1px solid ${C.amber}` };

            return (
              <button
                key={d}
                type="button"
                disabled={!ok && !isStart && !isEnd}
                onClick={() => click(d)}
                title={isBooked ? "Already booked" : undefined}
                className="text-sm rounded-md"
                style={{ height: 34, cursor: ok ? "pointer" : "not-allowed", ...style }}
              >
                {day}
              </button>
            );
          })}
        </div>
      </div>
    );
  }

  const second = next(view);
  const legend = [
    { label: "Available", style: { border: `1px solid ${C.border}` } },
    { label: "Booked", style: { background: C.border } },
    { label: "Your dates", style: { background: C.amber } },
  ];

  return (
    <div className="mt-5 rounded-lg p-4" style={{ background: C.panelAlt, border: `1px solid ${C.border}` }}>
      <p className="text-sm font-semibold mb-1">Availability</p>
      <p className="text-xs mb-4" style={{ color: C.muted }}>
        {!carId
          ? "Choose a car above to see which days are already booked."
          : mode === "end"
          ? "Now click your end date, the day you return the car."
          : "Click your start date, then your end date."}
      </p>

      <div className="grid md:grid-cols-2 gap-6">
        <Month y={view.y} m={view.m} showPrev showNext={false} />
        <Month y={second.y} m={second.m} showPrev={false} showNext />
      </div>

      <div className="flex gap-5 mt-4 text-xs flex-wrap" style={{ color: C.muted }}>
        {legend.map((l) => (
          <span key={l.label} className="flex items-center gap-2">
            <span className="rounded" style={{ width: 14, height: 14, display: "inline-block", ...l.style }} />
            {l.label}
          </span>
        ))}
      </div>
    </div>
  );
}