import { useEffect, useState } from "react";
import {
  Link,
  NavLink,
  useLocation,
} from "react-router-dom";

const navigationItems = [
  {
    label: "Find journeys",
    to: "/journeys/search",
  },
  {
    label: "Manage bookings",
    to: "/manage-booking",
  },
];

export function NavBar() {
  const location = useLocation();

  const [mobileMenuOpen, setMobileMenuOpen] =
    useState(false);

  useEffect(() => {
    setMobileMenuOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (!mobileMenuOpen) {
      return;
    }

    function handleEscape(event: KeyboardEvent) {
      if (event.key === "Escape") {
        setMobileMenuOpen(false);
      }
    }

    window.addEventListener(
      "keydown",
      handleEscape,
    );

    return () => {
      window.removeEventListener(
        "keydown",
        handleEscape,
      );
    };
  }, [mobileMenuOpen]);

  return (
    <header className="navbar">
      <div className="navbar__container">
        <Link
          to="/journeys/search"
          className="navbar__brand"
          aria-label="IronBus home"
        >
          <span
            className="navbar__brand-mark"
            aria-hidden="true"
          >
            IB
          </span>

          <span className="navbar__brand-copy">
            <strong>IronBus</strong>
            <small>Reserved train seats</small>
          </span>
        </Link>

        <nav
          className="navbar__desktop-navigation"
          aria-label="Primary navigation"
        >
          {navigationItems.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                isActive
                  ? "navbar__link navbar__link--active"
                  : "navbar__link"
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="navbar__actions">
          <Link
            to="/manage-booking"
            className="button button--secondary navbar__manage-button"
          >
            Find my booking
          </Link>

          <button
            type="button"
            className="navbar__menu-button"
            aria-label={
              mobileMenuOpen
                ? "Close navigation menu"
                : "Open navigation menu"
            }
            aria-expanded={mobileMenuOpen}
            aria-controls="mobile-navigation"
            onClick={() =>
              setMobileMenuOpen(current => !current)
            }
          >
            <span aria-hidden="true">
              {mobileMenuOpen ? "×" : "☰"}
            </span>
          </button>
        </div>
      </div>

      {mobileMenuOpen && (
        <nav
          id="mobile-navigation"
          className="navbar__mobile-navigation"
          aria-label="Mobile navigation"
        >
          <div className="navbar__mobile-navigation-inner">
            {navigationItems.map(item => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  isActive
                    ? "navbar__mobile-link navbar__mobile-link--active"
                    : "navbar__mobile-link"
                }
              >
                {item.label}
              </NavLink>
            ))}
          </div>
        </nav>
      )}
    </header>
  );
}