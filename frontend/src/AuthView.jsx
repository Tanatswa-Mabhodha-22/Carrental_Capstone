import React, { useState } from "react";
import { C } from "./theme";
import { API_BASE, ProfileField, formatLicence, LICENCE_RE, PHONE_RE, EMAIL_RE } from "./shared";

const EMPTY = { name: "", email: "", password: "", confirm: "", licenseNumber: "", phoneNumber: "", region: "" };

export default function AuthView({ onAuth }) {
  const [mode, setMode] = useState("login"); // "login" | "register"
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const set = (k) => (v) => setForm((f) => ({ ...f, [k]: v }));

  const emailOk = EMAIL_RE.test(form.email.trim());
  const passOk = form.password.length >= 6;
  const licenceOk = LICENCE_RE.test(form.licenseNumber);
  const phoneOk = PHONE_RE.test(form.phoneNumber);
  const canLogin = emailOk && form.password.length > 0;
  const canRegister =
    form.name.trim() && emailOk && passOk && form.password === form.confirm && licenceOk && phoneOk && form.region.trim();

  function switchMode(next) {
    setMode(next);
    setError("");
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      const isLogin = mode === "login";
      const res = await fetch(`${API_BASE}/member/${isLogin ? "login" : "register"}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(
          isLogin
            ? { email: form.email.trim(), password: form.password }
            : {
                name: form.name.trim(),
                email: form.email.trim(),
                password: form.password,
                licenseNumber: form.licenseNumber,
                phoneNumber: form.phoneNumber,
                region: form.region.trim(),
              }
        ),
      });
      if (res.status === 401) throw new Error("Wrong email or password");
      if (res.status === 409) throw new Error("That email is already registered. Try logging in.");
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg && msg.length < 120 ? msg : `Something went wrong (${res.status})`);
      }
      onAuth(await res.json());
    } catch (err) {
      setError(err instanceof TypeError ? "Can't reach the server. Is the backend running?" : err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <section className="max-w-md mx-auto">
      <h1 className="text-3xl font-extrabold mb-1 text-center">{mode === "login" ? "Welcome back" : "Create your account"}</h1>
      <p className="text-sm mb-8 text-center" style={{ color: C.muted }}>
        {mode === "login" ? "Log in to book cars and manage your rentals." : "Register once, then log in any time."}
      </p>

      <form onSubmit={submit} className="rounded-xl p-6 flex flex-col gap-4" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
        {mode === "register" && <ProfileField label="Full Name" value={form.name} onChange={set("name")} placeholder="Your name" />}

        <ProfileField
          label="Email"
          type="email"
          value={form.email}
          onChange={set("email")}
          placeholder="you@example.com"
          error={mode === "register" && form.email && !emailOk ? "Enter a valid email address" : ""}
        />
        <ProfileField
          label="Password"
          type="password"
          value={form.password}
          onChange={set("password")}
          placeholder={mode === "register" ? "At least 6 characters" : "Your password"}
          error={mode === "register" && form.password && !passOk ? "Use at least 6 characters" : ""}
        />

        {mode === "register" && (
          <>
            <ProfileField
              label="Confirm Password"
              type="password"
              value={form.confirm}
              onChange={set("confirm")}
              placeholder="Type the password again"
              error={form.confirm && form.confirm !== form.password ? "Passwords don't match" : ""}
            />
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
          </>
        )}

        {error && (
          <p className="text-sm rounded-lg px-3 py-2" style={{ color: C.red, background: C.redBg, border: `1px solid ${C.redBorder}` }}>
            {error}
          </p>
        )}

        <button
          type="submit"
          disabled={busy || !(mode === "login" ? canLogin : canRegister)}
          className="tm-btn w-full py-3 rounded-lg text-sm font-bold mt-2"
          style={{ background: C.amber, color: C.onAmber }}
        >
          {busy ? "PLEASE WAIT…" : mode === "login" ? "LOG IN" : "CREATE ACCOUNT"}
        </button>

        <p className="text-sm text-center" style={{ color: C.muted }}>
          {mode === "login" ? "New here? " : "Already have an account? "}
          <button type="button" onClick={() => switchMode(mode === "login" ? "register" : "login")} className="font-semibold" style={{ color: C.amber }}>
            {mode === "login" ? "Create an account" : "Log in"}
          </button>
        </p>
      </form>
    </section>
  );
}