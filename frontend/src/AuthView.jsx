import React, { useState } from "react";
import { C } from "./theme";
import { API_BASE, ProfileField, formatLicence, LICENCE_RE, PHONE_RE, EMAIL_RE, errorText } from "./shared";

const EMPTY = { name: "", email: "", password: "", confirm: "", licenseNumber: "", phoneNumber: "", region: "" };

export default function AuthView({ onAuth }) {
  const [mode, setMode] = useState("login"); // "login" | "register" | "forgot"
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [busy, setBusy] = useState(false);
  const set = (k) => (v) => setForm((f) => ({ ...f, [k]: v }));

  const emailOk = EMAIL_RE.test(form.email.trim());
  const passOk = form.password.length >= 6;
  const licenceOk = LICENCE_RE.test(form.licenseNumber);
  const phoneOk = PHONE_RE.test(form.phoneNumber);
  const matchOk = form.password === form.confirm;
  const canLogin = emailOk && form.password.length > 0;
  const canRegister = form.name.trim() && emailOk && passOk && matchOk && licenceOk && phoneOk && form.region.trim();
  const canReset = emailOk && licenceOk && phoneOk && passOk && matchOk;
  const canSubmit = mode === "login" ? canLogin : mode === "register" ? canRegister : canReset;

  function switchMode(next) {
    setMode(next);
    setError("");
    if (next !== "login") setNotice("");
    setForm((f) => ({ ...EMPTY, email: f.email })); // keep the email, clear the rest
  }

  async function submit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      if (mode === "forgot") {
        const res = await fetch(`${API_BASE}/member/forgot-password`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            email: form.email.trim(),
            licenseNumber: form.licenseNumber,
            phoneNumber: form.phoneNumber,
            newPassword: form.password,
          }),
        });
        if (!res.ok) throw new Error(await errorText(res, `Could not reset the password (${res.status})`));
        setMode("login");
        setForm((f) => ({ ...EMPTY, email: f.email }));
        setNotice("Your password was changed. Log in with your new password.");
        return;
      }

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
      if (!res.ok) throw new Error(await errorText(res, `Something went wrong (${res.status})`));
      const data = await res.json();
      onAuth(data.member, data.token);
    } catch (err) {
      setError(err instanceof TypeError ? "Can't reach the server. Is the backend running?" : err.message);
    } finally {
      setBusy(false);
    }
  }

  const titles = {
    login: ["Welcome back", "Log in to book cars and manage your rentals."],
    register: ["Create your account", "Register once, then log in any time."],
    forgot: ["Reset your password", "Enter the email, licence number and phone number on your account, then choose a new password."],
  };
  const showLicencePhone = mode === "register" || mode === "forgot";

  return (
    <section className="max-w-md mx-auto">
      <h1 className="text-3xl font-extrabold mb-1 text-center">{titles[mode][0]}</h1>
      <p className="text-sm mb-8 text-center" style={{ color: C.muted }}>
        {titles[mode][1]}
      </p>

      <form onSubmit={submit} className="rounded-xl p-6 flex flex-col gap-4" style={{ background: C.panel, border: `1px solid ${C.border}` }}>
        {notice && (
          <p className="text-sm rounded-lg px-3 py-2" style={{ color: C.green, background: C.greenBg, border: `1px solid ${C.greenBorder}` }}>
            {notice}
          </p>
        )}

        {mode === "register" && <ProfileField label="Full Name" value={form.name} onChange={set("name")} placeholder="Your name" />}

        <ProfileField
          label="Email"
          type="email"
          value={form.email}
          onChange={set("email")}
          placeholder="you@example.com"
          error={mode !== "login" && form.email && !emailOk ? "Enter a valid email address" : ""}
        />

        {showLicencePhone && (
          <>
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
          </>
        )}

        <ProfileField
          label={mode === "forgot" ? "New Password" : "Password"}
          type="password"
          value={form.password}
          onChange={set("password")}
          placeholder={mode === "login" ? "Your password" : "At least 6 characters"}
          error={mode !== "login" && form.password && !passOk ? "Use at least 6 characters" : ""}
        />

        {mode !== "login" && (
          <ProfileField
            label={mode === "forgot" ? "Confirm New Password" : "Confirm Password"}
            type="password"
            value={form.confirm}
            onChange={set("confirm")}
            placeholder="Type the password again"
            error={form.confirm && !matchOk ? "Passwords don't match" : ""}
          />
        )}

        {mode === "register" && <ProfileField label="Region" value={form.region} onChange={set("region")} placeholder="e.g. Cape Town, South Africa" />}

        {error && (
          <p className="text-sm rounded-lg px-3 py-2" style={{ color: C.red, background: C.redBg, border: `1px solid ${C.redBorder}` }}>
            {error}
          </p>
        )}

        <button type="submit" disabled={busy || !canSubmit} className="tm-btn w-full py-3 rounded-lg text-sm font-bold mt-2" style={{ background: C.amber, color: C.onAmber }}>
          {busy ? "PLEASE WAIT…" : mode === "login" ? "LOG IN" : mode === "register" ? "CREATE ACCOUNT" : "RESET PASSWORD"}
        </button>

        {mode === "login" && (
          <p className="text-sm text-center" style={{ color: C.muted }}>
            <button type="button" onClick={() => switchMode("forgot")} className="font-semibold" style={{ color: C.amber }}>
              Forgot your password?
            </button>
          </p>
        )}

        <p className="text-sm text-center" style={{ color: C.muted }}>
          {mode === "login" ? "New here? " : mode === "register" ? "Already have an account? " : "Remembered it? "}
          <button type="button" onClick={() => switchMode(mode === "login" ? "register" : "login")} className="font-semibold" style={{ color: C.amber }}>
            {mode === "login" ? "Create an account" : "Log in"}
          </button>
        </p>
      </form>
    </section>
  );
}