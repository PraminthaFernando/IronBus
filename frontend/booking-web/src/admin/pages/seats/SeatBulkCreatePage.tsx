import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate, useParams } from "react-router-dom";
import { bulkSeatSchema, type BulkSeatFormValues } from "../../schemas/forms";
import { mutations } from "../../hooks/resource-hooks";
import { PageHeader } from "../../components/ui";
export function SeatBulkCreatePage() {
  const { coachId = "" } = useParams();
  const nav = useNavigate(),
    m = mutations.seatBulk(coachId),
    form = useForm<BulkSeatFormValues>({
      resolver: zodResolver(bulkSeatSchema),
      defaultValues: {
        rows: 10,
        columnSuffixes: "A,B,C,D",
        seatType: "OTHER",
      },
    });
  return (
    <>
      <PageHeader title="Bulk create seats" eyebrow="Inventory" />
      <section className="panel form-panel">
        <form
          onSubmit={form.handleSubmit(async (v) => {
            await m.mutateAsync(v);
            nav(`/admin/coaches/${coachId}/seats`);
          })}
        >
          <label>
            Rows
            <input type="number" {...form.register("rows")} />
          </label>
          <label>
            Column suffixes
            <input {...form.register("columnSuffixes")} />
          </label>
          <label>
            Type
            <select {...form.register("seatType")}>
              <option>STANDARD</option>
              <option>WINDOW</option>
              <option>AISLE</option>
              <option>ACCESSIBLE</option>
            </select>
          </label>
          <div className="form-actions">
            <button
              type="button"
              onClick={() => nav(`/admin/coaches/${coachId}/seats`)}
            >
              Cancel
            </button>
            <button type="submit">Create</button>
          </div>
        </form>
      </section>
    </>
  );
}
