export function ErrorMessage({ message }: { message: string }) {
  return (
    <div role="alert" className="error-box">
      {message}
    </div>
  );
}
