import React, { useState } from "react";
import { C } from "./theme";
import { API_BASE, ProfileField, apiFetch, errorText } from "./shared";

const EMPTY = { current: "", next: "", confirm: "" };

/** "Change password" box for the My Account page. */
export default function ChangePasswordCard({ user }) {
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState("");
  const [done, setDone] = useState(false);
  const [busy, setBusy] = useState(false);
  const set = (k) => (v) => setForm((f) => ({ ...f, [k]: v }));

  const nextOk = form.next.length >= 6;
  const canSave = form.current && nextOk && form.next === form.confirm && form.next !== form.current;

  function close() {
    setOpen(false);
    setForm(EMPTY);
    setError("");
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    setDone(false);
    setBusy(true);
    try {
      const res = await apiFetch(`${API_BASE}/member/${user.memberId}/password`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ currentPassword: form.current, newPassword: form.next }),
      });
      if (!res.ok) throw new Error(await errorText(res, `Could not change the password (${res.status})`));
      setForm(EMPTY);
      setOpen(false);
      setDone(true);
    } catch (err) {
      setError(err instanceof TypeError ? "Can't reach the server. Is the backend running?" : err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="rounded-xl p-5 mb-8" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div>
          <p className="font-bold">Password</p>
          <p className="text-xs" style={{ color: done ? C.green : C.faint }}>
            {done ? "Your password was changed." : "Change the password you use to log in."}
          </p>
        </div>
        {!open && (
          <button
            onClick={() => {
              setDone(false);
              setOpen(true);
            }}
            className="tm-btn text-xs font-semibold px-4 py-2 rounded-lg"
            style={{ border: `1px solid ${C.border}`, color: C.text }}
          >
            Change Password
          </button>
        )}
      </div>

      {open && (
        <form onSubmit={submit} className="grid sm:grid-cols-3 gap-4 mt-5">
          <ProfileField label="Current Password" type="password" value={form.current} onChange={set("current")} placeholder="Current password" />
          <ProfileField
            label="New Password"
            type="password"
            value={form.next}
            onChange={set("next")}
            placeholder="At least 6 characters"
            error={form.next && !nextOk ? "Use at least 6 characters" : form.next && form.next === form.current ? "Must be different from the current one" : ""}
          />
          <ProfileField
            label="Confirm New Password"
            type="password"
            value={form.confirm}
            onChange={set("confirm")}
            placeholder="Type it again"
            error={form.confirm && form.confirm !== form.next ? "Passwords don't match" : ""}
          />
          {error && (
            <p className="sm:col-span-3 text-sm rounded-lg px-3 py-2" style={{ color: C.red, background: C.redBg, border: `1px solid ${C.redBorder}` }}>
              {error}
            </p>
          )}
          <div className="sm:col-span-3 flex gap-3">
            <button type="submit" disabled={busy || !canSave} className="tm-btn text-sm font-bold px-5 py-2.5 rounded-lg" style={{ background: C.amber, color: C.onAmber }}>
              {busy ? "SAVING…" : "SAVE PASSWORD"}
            </button>
            <button type="button" onClick={close} className="tm-btn text-sm font-semibold px-5 py-2.5 rounded-lg" style={{ border: `1px solid ${C.border}`, color: C.text }}>
              Cancel
            </button>
          </div>
        </form>
      )}
    </div>
  );
}