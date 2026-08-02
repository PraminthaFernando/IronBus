import { useState } from "react";

import { formatCurrency } from "../../lib/currency";
import { formatDateTime } from "../../lib/date-time";

import type { BookingResponse } from "../../types/domain";

interface BookingConfirmationProps {
  booking: BookingResponse;
}

function formatEnumLabel(value: string): string {
  return value
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/\b\w/g, character =>
      character.toUpperCase(),
    );
}

export function BookingConfirmation({
  booking,
}: BookingConfirmationProps) {
  const [copied, setCopied] = useState(false);

  async function copyReference() {
    try {
      await navigator.clipboard.writeText(
        booking.reference,
      );

      setCopied(true);

      window.setTimeout(() => {
        setCopied(false);
      }, 2_000);
    } catch {
      setCopied(false);
    }
  }

  const isConfirmed =
    booking.status === "CONFIRMED";

  const isCancelled =
    booking.status === "CANCELLED";

  return (
    <section className="booking-confirmation">
      <header className="booking-confirmation__hero">
        <div
          className={
            isCancelled
              ? "booking-confirmation__icon booking-confirmation__icon--cancelled"
              : "booking-confirmation__icon"
          }
          aria-hidden="true"
        >
          {isCancelled ? "!" : "✓"}
        </div>

        <span className="booking-confirmation__eyebrow">
          {isConfirmed
            ? "Reservation successful"
            : "Booking status"}
        </span>

        <h1>
          {isConfirmed
            ? "Your booking is confirmed"
            : isCancelled
              ? "This booking has been cancelled"
              : "Booking details"}
        </h1>

        <p>
          {isConfirmed
            ? "Your reserved seat has been successfully confirmed."
            : "Review the current status and details of this booking."}
        </p>
      </header>

      <section className="booking-reference-card">
        <div>
          <span className="booking-reference-card__label">
            Booking reference
          </span>

          <strong>
            {booking.reference}
          </strong>
        </div>

        <button
          type="button"
          className="button button--secondary"
          onClick={copyReference}
        >
          {copied ? "Copied" : "Copy reference"}
        </button>
      </section>

      <div className="booking-confirmation__layout">
        <section className="confirmation-card confirmation-card--journey">
          <div className="confirmation-card__header">
            <div>
              <span>Journey</span>
              <h2>
                {booking.originName}
                {" → "}
                {booking.destinationName}
              </h2>
            </div>

            <span
              className={[
                "booking-status-badge",
                `booking-status-badge--${booking.status.toLowerCase()}`,
              ].join(" ")}
            >
              {formatEnumLabel(booking.status)}
            </span>
          </div>

          <dl className="confirmation-details-grid">
            <div>
              <dt>Departure</dt>
              <dd>
                {formatDateTime(
                  booking.departureTime,
                )}
              </dd>
            </div>

            <div>
              <dt>Route</dt>
              <dd>
                {booking.originCode}
                {" → "}
                {booking.destinationCode}
              </dd>
            </div>

            <div>
              <dt>Travel class</dt>
              <dd>
                {formatEnumLabel(
                  booking.travelClass,
                )}
              </dd>
            </div>

            <div>
              <dt>Booked at</dt>
              <dd>
                {formatDateTime(
                  booking.createdAt,
                )}
              </dd>
            </div>
          </dl>
        </section>

        <aside className="confirmation-card confirmation-card--seat">
          <span className="confirmation-card__eyebrow">
            Reserved seat
          </span>

          <div className="confirmation-seat">
            <div className="confirmation-seat__number">
              {booking.seatNumber}
            </div>

            <div>
              <strong>
                Coach {booking.coachNumber}
              </strong>

              <span>
                Seat {booking.seatNumber}
              </span>
            </div>
          </div>

          <div className="confirmation-fare">
            <span>Total fare</span>

            <strong>
              {formatCurrency(
                booking.fareAmount,
                booking.currency,
              )}
            </strong>
          </div>
        </aside>
      </div>

      {isConfirmed && (
        <section className="confirmation-advice">
          <strong>
            Keep your booking reference safe
          </strong>

          <p>
            You will need this reference to find,
            manage, or cancel your booking later.
          </p>
        </section>
      )}
    </section>
  );
}