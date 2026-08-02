import type { AvailableSeat } from "../../types/domain";
import { formatCurrency } from "../../lib/currency";

export function AvailableSeatCard({
  seat,
  selected,
  onSelect,
}: {
  seat: AvailableSeat;
  selected: boolean;
  onSelect: (id: string) => void;
}) {
  return (
    <button
      type="button"
      aria-pressed={selected}
      className={selected ? "seat selected" : "seat"}
      onClick={() => onSelect(seat.seatId)}
    >
      <strong>{seat.seatNumber}</strong>
      <span>{seat.seatType}</span>
      <span>{formatCurrency(seat.fareAmount, seat.currency)}</span>
    </button>
  );
}
