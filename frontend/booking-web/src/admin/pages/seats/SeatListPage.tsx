import { useParams, Link } from "react-router-dom";
import { useSeats, useCoach } from "../../hooks/resource-hooks";
import { PageHeader, Loading, ErrorState } from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function SeatListPage() {
  const { coachId = "" } = useParams();
  const c = useCoach(coachId),
    q = useSeats(coachId, { page: 0, size: 200 });
  if (c.isPending || q.isPending) return <Loading />;
  if (c.isError) return <ErrorState error={c.error as ApiError} />;
  if (q.isError) return <ErrorState error={q.error as ApiError} />;
  return (
    <>
      <PageHeader
        eyebrow="Inventory"
        title={`Coach ${c.data.coachNumber} seats`}
        actions={
          <>
            <Link to={`/admin/coaches/${coachId}/seats/bulk`}>Bulk create</Link>
            <Link
              className="primary"
              to={`/admin/coaches/${coachId}/seats/new`}
            >
              Add seat
            </Link>
          </>
        }
      />
      <section className="panel">
        <div className="seat-grid">
          {q.data.content.map((s) => (
            <Link
              key={s.id}
              to={`/admin/seats/${s.id}/edit?coachId=${coachId}`}
              className={s.active ? "seat" : "seat inactive-seat"}
            >
              {s.seatNumber}
            </Link>
          ))}
        </div>
      </section>
    </>
  );
}
