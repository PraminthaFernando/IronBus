import { useState } from "react";
import { Link } from "react-router-dom";
import { useRoutes } from "../../hooks/resource-hooks";
import {
  ErrorState,
  Loading,
  PageHeader,
  Pagination,
  Status,
} from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function RouteListPage() {
  const [page, setPage] = useState(0);
  const q = useRoutes({ page, size: 20 });
  return (
    <>
      <PageHeader
        eyebrow="Network"
        title="Routes"
        description="Manage routes and station order."
        actions={
          <Link className="primary" to="/admin/routes/new">
            Add route
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
                  <th>Code</th>
                  <th>Name</th>
                  <th>Stations</th>
                  <th>Distance</th>
                  <th>Status</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {q.data.content.map((x) => (
                  <tr key={x.id}>
                    <td>{x.code}</td>
                    <td>{x.name}</td>
                    <td>{x.stationCount}</td>
                    <td>{x.totalDistanceKm} km</td>
                    <td>
                      <Status active={x.active} />
                    </td>
                    <td>
                      <Link to={`/admin/routes/${x.id}/edit`}>Edit</Link> ·{" "}
                      <Link to={`/admin/routes/${x.id}/stations`}>
                        Stations
                      </Link>
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
