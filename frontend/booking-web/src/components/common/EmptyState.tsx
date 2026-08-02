import type { ReactNode } from "react";

interface EmptyStateProps {
  title: string;
  message: string;
  icon?: ReactNode;
  action?: ReactNode;
}

export function EmptyState({
  title,
  message,
  icon,
  action,
}: EmptyStateProps) {
  return (
    <section
      className="empty-state"
      aria-labelledby="empty-state-title"
    >
      {icon && (
        <div
          className="empty-state__icon"
          aria-hidden="true"
        >
          {icon}
        </div>
      )}

      <h2
        id="empty-state-title"
        className="empty-state__title"
      >
        {title}
      </h2>

      <p className="empty-state__message">
        {message}
      </p>

      {action && (
        <div className="empty-state__action">
          {action}
        </div>
      )}
    </section>
  );
}