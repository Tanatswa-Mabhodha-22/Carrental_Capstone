import { Car as CarIcon, Sun, Moon, MapPin } from "lucide-react";
import { C, themeVars } from "./theme";

const GLOBAL_CSS = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono:wght@500&display=swap');
  .tm-mono { font-family: 'JetBrains Mono', monospace; }
  .tm-input::placeholder { color: ${C.faint}; }
  .tm-btn { transition: filter 150ms ease, transform 150ms ease, border-color 150ms ease; }
  .tm-btn:active { transform: scale(0.98); }
  .tm-btn:disabled { opacity: 0.4; cursor: not-allowed; }
  .tm-card { transition: border-color 150ms ease; }
  .tm-card:hover { border-color: ${C.amberHover}; }
`;

/**
 * Shared page layout: header (logo, nav, theme toggle), main content area, footer.
 * Props: nav [{id,label}], tab, onTabChange, theme, onToggleTheme, user, onLogout, children
 */
export default function Layout({ nav, tab, onTabChange, theme, onToggleTheme, user, onLogout, children }) {
  return (
    <div
      style={{
        ...themeVars(theme),
        colorScheme: theme,
        background: C.bg,
        color: C.text,
        fontFamily: "'Inter', system-ui, sans-serif",
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <style>{GLOBAL_CSS}</style>

      <header style={{ background: C.panel, borderBottom: `1px solid ${C.border}` }}>
        <div className="max-w-6xl mx-auto flex items-center justify-between px-8 py-6 flex-wrap gap-4">
          <div className="flex items-center gap-3">
            <div className="flex items-center justify-center rounded-lg" style={{ width: 44, height: 44, background: C.amber }}>
              <CarIcon size={24} color={C.onAmber} />
            </div>
            <p className="text-xl font-extrabold tracking-tight">CarRental</p>
          </div>

          <nav className="flex items-center gap-8">
            {nav.map((n) => (
              <button
                key={n.id}
                onClick={() => onTabChange(n.id)}
                className="text-base font-medium pb-1"
                style={{ color: tab === n.id ? C.amber : C.muted, borderBottom: tab === n.id ? `2px solid ${C.amber}` : "2px solid transparent" }}
              >
                {n.label}
              </button>
            ))}
            {user && (
              <>
                <span className="text-sm hidden sm:inline" style={{ color: C.muted }}>
                  {user.role === "ADMIN" ? "Admin" : `Hi, ${(user.name || "").split(" ")[0]}`}
                </span>
                <button onClick={onLogout} className="text-base font-medium pb-1" style={{ color: C.muted, borderBottom: "2px solid transparent" }}>
                  Logout
                </button>
              </>
            )}
            <button
              onClick={onToggleTheme}
              aria-label={theme === "dark" ? "Switch to light mode" : "Switch to dark mode"}
              title={theme === "dark" ? "Light mode" : "Night mode"}
              className="tm-btn flex items-center justify-center rounded-lg"
              style={{ width: 38, height: 38, border: `1px solid ${C.border}`, color: C.text, background: C.panelAlt }}
            >
              {theme === "dark" ? <Sun size={18} /> : <Moon size={18} />}
            </button>
          </nav>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-8 py-10 w-full" style={{ flex: 1 }}>
        {children}
      </main>

      <footer style={{ background: C.panel, borderTop: `1px solid ${C.border}` }}>
        <div className="max-w-6xl mx-auto px-8 py-6 flex items-center justify-between flex-wrap gap-2 text-sm" style={{ color: C.muted }}>
          <span>© {new Date().getFullYear()} CarRental. All rights reserved.</span>
          <span className="flex items-center gap-1.5">
            <MapPin size={14} /> Cape Town, South Africa
          </span>
        </div>
      </footer>
    </div>
  );
}