export function BookingConflictDialog({
  open,
  onClose,
}: {
  open: boolean;
  onClose: () => void;
}) {
  if (!open) return null;
  return (
    <div role="alertdialog" aria-modal="true" className="dialog">
      <h2>Seat no longer available</h2>
      <p>
        Another passenger booked this seat. Availability has been refreshed.
      </p>
      <button onClick={onClose}>Select another seat</button>
    </div>
  );
}
