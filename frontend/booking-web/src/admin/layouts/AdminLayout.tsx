import { Outlet, Link } from "react-router-dom";
import { Sidebar } from "../components/ui";
import "../styles/admin.css";

export function AdminLayout() {
  return (
    <div className="admin-shell">
      <Sidebar />
      <div>
        <header className="admin-topbar">
          <strong>Administration console</strong>
          <Link to="/">Booking site</Link>
        </header>
        <main className="admin-main">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
