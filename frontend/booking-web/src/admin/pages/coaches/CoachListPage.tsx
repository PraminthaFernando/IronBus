import { useParams, Link } from "react-router-dom";
import { useCoaches, useTrain } from "../../hooks/resource-hooks";
import { PageHeader, Loading, ErrorState, Status } from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function CoachListPage() {
  const { trainId = "" } = useParams();
  const t = useTrain(trainId),
    q = useCoaches(trainId, { page: 0, size: 100 });
  if (t.isPending || q.isPending) return <Loading />;
  if (t.isError) return <ErrorState error={t.error as ApiError} />;
  if (q.isError) return <ErrorState error={q.error as ApiError} />;
  return (
    <>
      <PageHeader
        eyebrow="Fleet"
        title={`${t.data.code} coaches`}
        actions={
          <Link className="primary" to={`/admin/trains/${trainId}/coaches/new`}>
            Add coach
          </Link>
        }
      />
      <section className="panel">
        <table>
          <thead>
            <tr>
              <th>Coach</th>
              <th>Class</th>
              <th>Mode</th>
              <th>Seats</th>
              <th>Status</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {q.data.content.map((x) => (
              <tr key={x.id}>
                <td>{x.coachNumber}</td>
                <td>{x.travelClass}</td>
                <td>{x.reservationMode}</td>
                <td>{x.seatCount}</td>
                <td>
                  <Status active={x.active} />
                </td>
                <td>
                  <Link to={`/admin/coaches/${x.id}/edit?trainId=${trainId}`}>
                    Edit
                  </Link>
                  {x.reservationMode === "RESERVED" && (
                    <>
                      {" "}
                      · <Link to={`/admin/coaches/${x.id}/seats`}>Seats</Link>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </>
  );
}
