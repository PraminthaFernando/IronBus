import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { seatSchema, type SeatFormValues } from "../../schemas/forms";
import { useSeat, mutations } from "../../hooks/resource-hooks";
import { PageHeader, Loading, ErrorState } from "../../components/ui";
import type { ApiError } from "../../../api/api-error";

export function SeatFormPage() {
  const { coachId: pathCoachId, seatId } = useParams();
  const [p] = useSearchParams();
  const coachId = pathCoachId ?? p.get("coachId") ?? "";

  const edit = !!seatId,
    nav = useNavigate(),
    detail = useSeat(seatId),
    create = mutations.seatCreate(coachId),
    update = mutations.seatUpdate(seatId ?? "");

  const form = useForm<SeatFormValues>({
    resolver: zodResolver(seatSchema),
    defaultValues: {
      seatNumber: "",
      seatType: "OTHER",
      rowNumber: 1,
      columnNumber: 1,
      active: true,
    },
  });

  useEffect(() => {
    if (detail.data)
      form.reset({
        ...detail.data,
        rowNumber: detail.data.rowNumber ?? 1,
        columnNumber: detail.data.columnNumber ?? 1,
      });
  }, [detail.data, form]);

  if (edit && detail.isPending) return <Loading />;

  if (edit && detail.isError) return <ErrorState error={detail.error as ApiError} />;

  const resolved = detail.data?.coachId ?? coachId;

  return (
    <>
      <PageHeader title={edit ? "Edit seat" : "Add seat"} eyebrow="Inventory" />
      <section className="panel form-panel">
        <form
          onSubmit={form.handleSubmit(async (v) => {
            if (edit)
              await update.mutateAsync({
                ...v,
                expectedVersion: detail.data!.version,
              });
            else await create.mutateAsync(v);
            nav(`/admin/coaches/${resolved}/seats`);
          })}
        >
          <label>
            Seat number
            <input {...form.register("seatNumber")} />
          </label>
          <label>
            Type
            <select {...form.register("seatType")}>
              <option>OTHER</option>
              <option>WINDOW</option>
              <option>AISLE</option>
              <option>MIDDLE</option>
            </select>
          </label>
          <label>
            Row
            <input type="number" {...form.register("rowNumber")} />
          </label>
          <label>
            Column
            <input type="number" {...form.register("columnNumber")} />
          </label>
          <label className="check">
            <input type="checkbox" {...form.register("active")} /> Active
          </label>
          <div className="form-actions">
            <button
              type="button"
              onClick={() => nav(`/admin/coaches/${resolved}/seats`)}
            >
              Cancel
            </button>
            <button type="submit">Save</button>
          </div>
        </form>
      </section>
    </>
  );
}
