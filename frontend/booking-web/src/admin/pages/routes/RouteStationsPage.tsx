import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { RouteStationAdminItem } from "../../types/admin";
import {
  useRoute,
  useRouteStations,
  mutations,
} from "../../hooks/resource-hooks";
import { PageHeader, Loading, ErrorState } from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function RouteStationsPage() {
  const { routeId = "" } = useParams();
  const r = useRoute(routeId),
    q = useRouteStations(routeId),
    m = mutations.routeStations(routeId);
  const [items, setItems] = useState<RouteStationAdminItem[]>([]);
  useEffect(() => {
    if (q.data) setItems(q.data);
  }, [q.data]);
  if (r.isPending || q.isPending) return <Loading />;
  if (r.isError) return <ErrorState error={r.error as ApiError} />;
  if (q.isError) return <ErrorState error={q.error as ApiError} />;
  const add = () =>
    setItems((v) => [
      ...v,
      {
        stationId: "",
        sequenceNumber: v.length,
        distanceFromOriginKm: v.length
          ? Number(v.at(-1)!.distanceFromOriginKm) + 1
          : 0,
        scheduledOffsetMinutes: v.length
          ? Number(v.at(-1)!.scheduledOffsetMinutes) + 1
          : 0,
      },
    ]);
  const patch = (i: number, p: Partial<RouteStationAdminItem>) =>
    setItems((v) => v.map((x, n) => (n === i ? { ...x, ...p } : x)));
  const remove = (i: number) =>
    setItems((v) =>
      v.filter((_, n) => n !== i).map((x, n) => ({ ...x, sequenceNumber: n })),
    );
  return (
    <>
      <PageHeader
        eyebrow="Route stations"
        title={r.data.name}
        description="Configure ordered stations, cumulative distance and schedule offsets."
        actions={<button onClick={add}>Add row</button>}
      />
      <section className="panel">
        {m.isError && <ErrorState error={m.error as ApiError} />}
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Station ID</th>
              <th>Distance km</th>
              <th>Offset min</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {items.map((x, i) => (
              <tr key={i}>
                <td>{i + 1}</td>
                <td>
                  <input
                    value={x.stationId}
                    onChange={(e) => patch(i, { stationId: e.target.value })}
                  />
                </td>
                <td>
                  <input
                    type="number"
                    value={x.distanceFromOriginKm}
                    onChange={(e) =>
                      patch(i, { distanceFromOriginKm: Number(e.target.value) })
                    }
                  />
                </td>
                <td>
                  <input
                    type="number"
                    value={x.scheduledOffsetMinutes}
                    onChange={(e) =>
                      patch(i, {
                        scheduledOffsetMinutes: Number(e.target.value),
                      })
                    }
                  />
                </td>
                <td>
                  <button onClick={() => remove(i)}>Remove</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="form-actions">
          <button
            disabled={items.length < 2 || m.isPending}
            onClick={() =>
              m.mutate({ stations: items, expectedVersion: r.data.version })
            }
          >
            {m.isPending ? "Saving…" : "Save order"}
          </button>
        </div>
      </section>
    </>
  );
}
