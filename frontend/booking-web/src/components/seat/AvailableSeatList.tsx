import type { AvailableSeat } from "../../types/domain";
import { AvailableSeatCard } from "./AvailableSeatCard";

export function AvailableSeatList({
  seats,
  selectedSeatId,
  onSelect,
}: {
  seats: AvailableSeat[];
  selectedSeatId: string | null;
  onSelect: (id: string) => void;
}) {
  const g = new Map<string, AvailableSeat[]>();
  for (const s of seats) {
    const a = g.get(s.coachId) ?? [];
    a.push(s);
    g.set(s.coachId, a);
  }

  return (
    <div>
      {[...g.entries()].map(([id, ss]) => (
        <section key={id}>
          <h2>
            Coach {ss[0].coachNumber} — {ss[0].travelClass}
          </h2>
          <div className="seat-grid">
            {ss.map((s) => (
              <AvailableSeatCard
                key={s.seatId}
                seat={s}
                selected={selectedSeatId === s.seatId}
                onSelect={onSelect}
              />
            ))}
          </div>
        </section>
      ))}
    </div>
  );
}
