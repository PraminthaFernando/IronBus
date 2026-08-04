import type { ReactNode } from "react";
import { Link, NavLink } from "react-router-dom";
import type { JourneyAdminSummary } from "../types/admin";
import type { ApiError } from "../../api/api-error";

export function PageHeader({
  eyebrow,
  title,
  description,
  actions,
}: {
  eyebrow?: string;
  title: string;
  description?: string;
  actions?: ReactNode;
}) {
  return (
    <header className="admin-page-header">
      <div>
        {eyebrow && <p className="eyebrow">{eyebrow}</p>}
        <h1>{title}</h1>
        {description && <p>{description}</p>}
      </div>
      {actions && <div className="actions">{actions}</div>}
    </header>
  );
}
export const Loading = ({ text = "Loading…" }: { text?: string }) => (
  <div className="admin-state" role="status">
    {text}
  </div>
);
export function ErrorState({
  error,
  retry,
}: {
  error: ApiError;
  retry?: () => void;
}) {
  return (
    <section className="admin-error" role="alert">
      <h2>Unable to complete the request</h2>
      <p>{error.message}</p>
      {error.traceId && (
        <p>
          Support reference: <code>{error.traceId}</code>
        </p>
      )}
      {retry && <button onClick={retry}>Try again</button>}
    </section>
  );
}
export const Status = ({ active }: { active: boolean }) => (
  <span className={`status ${active ? "active" : "inactive"}`}>
    {active ? "Active" : "Inactive"}
  </span>
);
export const JourneyStatus = ({
  status,
}: {
  status: JourneyAdminSummary["status"];
}) => <span className={`status ${status.toLowerCase()}`}>{status}</span>;
export function Pagination({
  page,
  totalPages,
  setPage,
}: {
  page: number;
  totalPages: number;
  setPage: (n: number) => void;
}) {
  if (totalPages <= 1) return null;
  return (
    <nav className="pagination">
      <button disabled={page === 0} onClick={() => setPage(page - 1)}>
        Previous
      </button>
      <span>
        Page {page + 1} of {totalPages}
      </span>
      <button
        disabled={page >= totalPages - 1}
        onClick={() => setPage(page + 1)}
      >
        Next
      </button>
    </nav>
  );
}
export function Sidebar() {
  const items = [
    ["Overview", "/admin"],
    ["Journeys", "/admin/journeys"],
    ["Routes", "/admin/routes"],
    ["Stations", "/admin/stations"],
    ["Trains", "/admin/trains"],
  ];
  return (
    <aside className="admin-sidebar">
      <div className="brand">
        <b>IB</b>
        <span>IronBus Admin</span>
      </div>
      <nav>
        <ul>
          {items.map(([l, to]) => (
            <li key={to}>
              <NavLink
                end={to === "/admin"}
                to={to}
                className={({ isActive }) => (isActive ? "active-link" : "")}
              >
                {l}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
}

export function Metric({
  label,
  value,
  detail,
  to,
}: {
  label: string;
  value: number;
  detail: string;
  to: string;
}) {
  return (
    <Link className="metric" to={to}>
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
    </Link>
  );
}
