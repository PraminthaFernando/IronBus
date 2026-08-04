import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { PageHeader, Loading, ErrorState } from "../../components/ui";
import { useTrain, mutations } from "../../hooks/resource-hooks";
import { trainSchema, type TrainFormValues } from "../../schemas/forms";
import type { ApiError } from "../../../api/api-error";
export function TrainFormPage() {
  const { trainId } = useParams();
  const edit = !!trainId;
  const nav = useNavigate();
  const detail = useTrain(trainId);
  const create = mutations.trainCreate();
  const update = mutations.trainUpdate(trainId ?? "");
  const form = useForm<TrainFormValues>({
    resolver: zodResolver(trainSchema),
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
      <PageHeader eyebrow="Trains" title={edit ? "Edit train" : "Add train"} />
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
            nav("/admin/trains");
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
            <button type="button" onClick={() => nav("/admin/trains")}>
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
