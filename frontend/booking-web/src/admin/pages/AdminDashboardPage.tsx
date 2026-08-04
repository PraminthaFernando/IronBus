import { Link } from "react-router-dom";
import { useDashboard } from "../hooks/use-admin";
import {
  ErrorState,
  JourneyStatus,
  Loading,
  Metric,
  PageHeader,
} from "../components/ui";
import type { ApiError } from "../../api/api-error";
export function AdminDashboardPage() {
  const q = useDashboard();
  if (q.isPending) return <Loading text="Loading dashboard…" />;
  if (q.isError)
    return <ErrorState error={q.error as ApiError} retry={() => q.refetch()} />;
  const d = q.data;
  return (
    <>
      <PageHeader
        eyebrow="Administration"
        title="Operations overview"
        description="Manage railway resources and upcoming services."
        actions={
          <Link className="primary" to="/admin/journeys/new">
            Schedule journey
          </Link>
        }
      />
      <section className="metrics">
        <Metric
          label="Active stations"
          value={d.stations.active}
          detail={`${d.stations.total} total`}
          to="/admin/stations"
        />
        <Metric
          label="Active routes"
          value={d.routes.active}
          detail={`${d.routes.total} total`}
          to="/admin/routes"
        />
        <Metric
          label="Active trains"
          value={d.trains.active}
          detail={`${d.trains.total} total`}
          to="/admin/trains"
        />
        <Metric
          label="Reserved coaches"
          value={d.coaches.reserved}
          detail={`${d.coaches.total} total`}
          to="/admin/trains"
        />
        <Metric
          label="Active seats"
          value={d.seats.active}
          detail={`${d.seats.total} total`}
          to="/admin/trains"
        />
        <Metric
          label="Upcoming journeys"
          value={d.journeys.upcoming}
          detail={`${d.journeys.today} today`}
          to="/admin/journeys"
        />
      </section>
      <div className="grid">
        <section className="panel">
          <h2>Upcoming journeys</h2>
          <table>
            <thead>
              <tr>
                <th>Departure</th>
                <th>Route</th>
                <th>Train</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {d.upcomingJourneys.map((j) => (
                <tr key={j.id}>
                  <td>{new Date(j.departureTime).toLocaleString()}</td>
                  <td>{j.routeCode}</td>
                  <td>{j.trainCode}</td>
                  <td>
                    <JourneyStatus status={j.status} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
        <section className="panel">
          <h2>Configuration warnings</h2>
          <ul>
            {d.warnings.map((w) => (
              <li key={w.code + w.resourceId}>
                <b>{w.resourceType}</b>
                <br />
                {w.message}
              </li>
            ))}
          </ul>
        </section>
      </div>
    </>
  );
}
