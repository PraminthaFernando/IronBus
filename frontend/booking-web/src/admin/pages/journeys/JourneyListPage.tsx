import { useState } from "react";
import { Link } from "react-router-dom";
import { useJourneys } from "../../hooks/resource-hooks";
import {
  ErrorState,
  JourneyStatus,
  Loading,
  PageHeader,
  Pagination,
} from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function JourneyListPage() {
  const [page, setPage] = useState(0);
  const q = useJourneys({ page, size: 20 });
  return (
    <>
      <PageHeader
        eyebrow="Operations"
        title="Journeys"
        description="Schedule and monitor services."
        actions={
          <Link className="primary" to="/admin/journeys/new">
            Schedule journey
          </Link>
        }
      />
      <section className="panel">
        {q.isPending && <Loading />}
        {q.isError && <ErrorState error={q.error as ApiError} />}{" "}
        {q.isSuccess && (
          <>
            <table>
              <thead>
                <tr>
                  <th>Departure</th>
                  <th>Route</th>
                  <th>Train</th>
                  <th>Status</th>
                  <th>Bookings</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {q.data.content.map((x) => (
                  <tr key={x.id}>
                    <td>{new Date(x.departureTime).toLocaleString()}</td>
                    <td>{x.routeCode}</td>
                    <td>{x.trainCode}</td>
                    <td>
                      <JourneyStatus status={x.status} />
                    </td>
                    <td>{x.bookingCount}</td>
                    <td>
                      <Link to={`/admin/journeys/${x.id}/edit`}>Open</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <Pagination
              page={q.data.page}
              totalPages={q.data.totalPages}
              setPage={setPage}
            />
          </>
        )}
      </section>
    </>
  );
}
