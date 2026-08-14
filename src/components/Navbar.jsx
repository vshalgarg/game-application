import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { FaBars, FaTimes } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";
import { useSnackbar } from "../context/SnackbarContext";
import GameZoneLogo from "./brand/GameZoneLogo";
import Button from "./ui/Button";

const navItems = [
  { label: "Home", path: "/" },
  { label: "About", path: "/about" },
  { label: "Contact", path: "/contact" },
  { label: "Profile", path: "/profile" },
];

const Navbar = () => {
  const navigate = useNavigate();
  const { auth, logout } = useAuth();
  const { showSnackbar } = useSnackbar();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    setMenuOpen(false);
    logout();
    showSnackbar("Logged out successfully", "success");
    navigate("/login", { replace: true });
  };

  const handleLogin = () => {
    setMenuOpen(false);
    navigate("/login", { replace: true });
  };

  const authButton = auth ? (
    <Button variant="nav" className="px-4 py-1.5 text-xs sm:px-5 sm:py-2 sm:text-sm" onClick={handleLogout}>
      Logout
    </Button>
  ) : (
    <Button variant="nav" className="px-4 py-1.5 text-xs sm:px-5 sm:py-2 sm:text-sm" onClick={handleLogin}>
      Login
    </Button>
  );

  return (
    <header className="gz-navbar">
      <div className="gz-navbar__inner">
        <button
          type="button"
          className="flex min-w-0 cursor-pointer items-center gap-2 bg-transparent"
          onClick={() => {
            setMenuOpen(false);
            navigate("/");
          }}
          aria-label="GameZone home"
        >
          <GameZoneLogo className="h-8 w-8 shrink-0" />
          <span className="gz-navbar__brand truncate text-lg font-bold tracking-wide text-gz-text md:text-xl">
            Game<span className="gz-text-neon">Zone</span>
          </span>
        </button>

        <div className="flex items-center gap-2 sm:gap-4">
          <nav className="hidden items-center gap-8 md:flex" aria-label="Main">
            {navItems.map(({ label, path }) => (
              <NavLink
                key={path}
                to={path}
                end={path === "/"}
                className={({ isActive }) =>
                  `gz-navbar__link ${isActive ? "gz-navbar__link--active" : ""}`
                }
              >
                {label}
              </NavLink>
            ))}
          </nav>

          {authButton}

          <button
            type="button"
            className="cursor-pointer bg-transparent p-2 text-gz-text md:hidden"
            onClick={() => setMenuOpen((open) => !open)}
            aria-label={menuOpen ? "Close menu" : "Open menu"}
            aria-expanded={menuOpen}
          >
            {menuOpen ? <FaTimes size={20} /> : <FaBars size={20} />}
          </button>
        </div>
      </div>

      {menuOpen && (
        <nav className="gz-navbar__menu" aria-label="Mobile">
          <div className="flex flex-col gap-3">
            {navItems.map(({ label, path }) => (
              <NavLink
                key={path}
                to={path}
                end={path === "/"}
                onClick={() => setMenuOpen(false)}
                className={({ isActive }) =>
                  `gz-navbar__link py-1 ${isActive ? "gz-navbar__link--active" : ""}`
                }
              >
                {label}
              </NavLink>
            ))}
          </div>
        </nav>
      )}
    </header>
  );
};

export default Navbar;
