import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { PageHeader, Loading, ErrorState } from "../../components/ui";
import { useStation, mutations } from "../../hooks/resource-hooks";
import { stationSchema, type StationFormValues } from "../../schemas/forms";
import type { ApiError } from "../../../api/api-error";

export function StationFormPage() {
  const { stationId } = useParams();
  const edit = !!stationId;
  const nav = useNavigate();
  const detail = useStation(stationId);
  const create = mutations.stationCreate();
  const update = mutations.stationUpdate(stationId ?? "");

  const form = useForm<StationFormValues>({
    resolver: zodResolver(stationSchema),
    defaultValues: { code: "", name: "", active: true },
  });
  
  useEffect(() => {
    if (detail.data)
      form.reset({
        code: detail.data.code,
        name: detail.data.name,
        active: detail.data.active,
      });
  }, [detail.data, form]);

  if (edit && detail.isPending) return <Loading />;
  if (edit && detail.isError) return <ErrorState error={detail.error as ApiError} />;
  const m = edit ? update : create;
  return (
    <>
      <PageHeader
        eyebrow="Stations"
        title={edit ? "Edit station" : "Add station"}
      />
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
            nav("/admin/stations");
          })}
        >
          <label>
            Code
            <input
              {...form.register("code")}
              onChange={(e) =>
                form.setValue("code", e.target.value.toUpperCase(), {
                  shouldValidate: true,
                })
              }
            />
          </label>
          <label>
            Name
            <input {...form.register("name")} />
          </label>
          <label className="check">
            <input type="checkbox" {...form.register("active")} /> Active
          </label>
          <div className="form-actions">
            <button type="button" onClick={() => nav("/admin/stations")}>
              Cancel
            </button>
            <button type="submit" disabled={m.isPending}>
              {m.isPending ? "Saving…" : "Save"}
            </button>
          </div>
        </form>
      </section>
    </>
  );
}
