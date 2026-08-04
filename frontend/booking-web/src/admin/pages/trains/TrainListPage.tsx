import { useState } from "react";
import { Link } from "react-router-dom";
import { useTrains } from "../../hooks/resource-hooks";
import {
  ErrorState,
  Loading,
  PageHeader,
  Pagination,
  Status,
} from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function TrainListPage() {
  const [page, setPage] = useState(0);
  const q = useTrains({ page, size: 20 });
  return (
    <>
      <PageHeader
        eyebrow="Fleet"
        title="Trains"
        description="Manage trains, coaches and seats."
        actions={
          <Link className="primary" to="/admin/trains/new">
            Add train
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
                  <th>Coaches</th>
                  <th>Seats</th>
                  <th>Status</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {q.data.content.map((x) => (
                  <tr key={x.id}>
                    <td>{x.code}</td>
                    <td>{x.name}</td>
                    <td>{x.coachCount}</td>
                    <td>{x.seatCount}</td>
                    <td>
                      <Status active={x.active} />
                    </td>
                    <td>
                      <Link to={`/admin/trains/${x.id}/edit`}>Edit</Link> ·{" "}
                      <Link to={`/admin/trains/${x.id}/coaches`}>Coaches</Link>
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
