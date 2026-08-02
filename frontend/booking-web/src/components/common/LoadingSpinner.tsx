export function LoadingSpinner({ label = "Loading..." }: { label?: string }) {
  return (
    <div role="status" className="status-box">
      {label}
    </div>
  );
}
