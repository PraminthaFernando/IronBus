import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { coachSchema, type CoachFormValues } from "../../schemas/forms";
import { useCoach, mutations } from "../../hooks/resource-hooks";
import { PageHeader, Loading, ErrorState } from "../../components/ui";
import type { ApiError } from "../../../api/api-error";
export function CoachFormPage() {
  const { trainId: pathTrainId, coachId } = useParams();
  const [p] = useSearchParams();
  const trainId = pathTrainId ?? p.get("trainId") ?? "";
  const edit = !!coachId,
    nav = useNavigate(),
    detail = useCoach(coachId),
    create = mutations.coachCreate(trainId),
    update = mutations.coachUpdate(coachId ?? "");
  const form = useForm<CoachFormValues>({
    resolver: zodResolver(coachSchema),
    defaultValues: {
      coachNumber: "",
      travelClass: "SECOND_CLASS",
      reservationMode: "RESERVED",
      active: true,
    },
  });
  useEffect(() => {
    if (detail.data) form.reset(detail.data);
  }, [detail.data, form]);
  if (edit && detail.isPending) return <Loading />;
  if (edit && detail.isError) return <ErrorState error={detail.error as ApiError} />;
  const resolved = detail.data?.trainId ?? trainId,
    m = edit ? update : create;
  return (
    <>
      <PageHeader title={edit ? "Edit coach" : "Add coach"} eyebrow="Fleet" />
      <section className="panel form-panel">
        {m.isError && <ErrorState error={m.error as ApiError} />}
        <form
          onSubmit={form.handleSubmit(async (v) => {
            if (edit)
              await update.mutateAsync({
                ...v,
                expectedVersion: detail.data!.version,
              });
            else await create.mutateAsync(v);
            nav(`/admin/trains/${resolved}/coaches`);
          })}
        >
          <label>
            Coach number
            <input {...form.register("coachNumber")} />
          </label>
          <label>
            Travel class
            <select {...form.register("travelClass")}>
              <option value="FIRST_CLASS">First</option>
              <option value="SECOND_CLASS">Second</option>
              <option value="THIRD_CLASS">Third</option>
            </select>
          </label>
          <label>
            Mode
            <select {...form.register("reservationMode")}>
              <option value="RESERVED">Reserved</option>
              <option value="UNRESERVED">Unreserved</option>
            </select>
          </label>
          <label className="check">
            <input type="checkbox" {...form.register("active")} /> Active
          </label>
          <div className="form-actions">
            <button
              type="button"
              onClick={() => nav(`/admin/trains/${resolved}/coaches`)}
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
