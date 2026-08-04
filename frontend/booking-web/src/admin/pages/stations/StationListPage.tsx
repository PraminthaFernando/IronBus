import { useState } from "react";
import { Link } from "react-router-dom";
import { useStations } from "../../hooks/resource-hooks";
import {
  ErrorState,
  Loading,
  PageHeader,
  Pagination,
  Status,
} from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function StationListPage() {
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const q = useStations({ search, page, size: 20 });
  return (
    <>
      <PageHeader
        eyebrow="Network"
        title="Stations"
        description="Manage railway stations."
        actions={
          <Link className="primary" to="/admin/stations/new">
            Add station
          </Link>
        }
      />
      <section className="panel">
        <input
          type="search"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search code or name"
        />
        {q.isPending && <Loading />}
        {q.isError && <ErrorState error={q.error as ApiError} />}{" "}
        {q.isSuccess && (
          <>
            <table>
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Name</th>
                  <th>Status</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {q.data.content.map((x) => (
                  <tr key={x.id}>
                    <td>{x.code}</td>
                    <td>{x.name}</td>
                    <td>
                      <Status active={x.active} />
                    </td>
                    <td>
                      <Link to={`/admin/stations/${x.id}/edit`}>Edit</Link>
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
