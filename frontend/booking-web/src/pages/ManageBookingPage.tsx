import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

import {
  useBooking,
  useBookingsByEmail,
  useCancelBooking,
} from "../hooks/use-booking";

import { PageContainer } from "../components/common/PageContainer";
import { BookingConfirmation } from "../components/booking/BookingConfirmation";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { EmptyState } from "../components/common/EmptyState";

import { getApiError } from "../api/error-utils";
import { formatCurrency } from "../lib/currency";
import { formatDateTime } from "../lib/date-time";

import type {
  BookingSearchItem,
} from "../types/domain";

type SearchMode = "REFERENCE" | "EMAIL";

const BOOKING_REFERENCE_PATTERN =
  /^LSF-\d{2}-[A-Z0-9]{6}$/;

const EMAIL_PATTERN =
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function ManageBookingPage() {
  const [searchParams, setSearchParams] =
    useSearchParams();

  const initialReference =
    searchParams.get("reference") ?? "";

  const initialEmail =
    searchParams.get("email") ?? "";

  const initialMode: SearchMode =
    initialEmail ? "EMAIL" : "REFERENCE";

  const [mode, setMode] =
    useState<SearchMode>(initialMode);

  const [input, setInput] = useState(
    initialEmail || initialReference,
  );

//   const [reference, setReference] =
//     useState(initialReference);

  const [email, setEmail] =
    useState(initialEmail);

  const [page, setPage] = useState(0);

  const [validationError, setValidationError] =
    useState<string | null>(null);

  const [selectedReference, setSelectedReference] =
    useState(initialReference);

  const [showCancelDialog, setShowCancelDialog] =
    useState(false);

  const [cancellationError, setCancellationError] =
    useState<string | null>(null);

  const bookingQuery = useBooking(
    selectedReference || undefined,
  );

  const emailSearchQuery =
    useBookingsByEmail(
      email || undefined,
      page,
      10,
    );

  const cancelMutation =
    useCancelBooking(selectedReference);

  useEffect(() => {
    if (initialEmail) {
      setMode("EMAIL");
      setInput(initialEmail);
      setEmail(initialEmail);
    } else if (initialReference) {
      setMode("REFERENCE");
      setInput(initialReference);
      // setReference(initialReference);
      setSelectedReference(initialReference);
    }
  }, [initialEmail, initialReference]);

  function handleModeChange(nextMode: SearchMode) {
    setMode(nextMode);
    setInput("");
    // setReference("");
    setEmail("");
    setSelectedReference("");
    setValidationError(null);
    setCancellationError(null);
    setPage(0);
    setSearchParams({});
  }

  function handleSearch() {
    const normalized = input.trim();

    setValidationError(null);
    setCancellationError(null);
    setSelectedReference("");
    setShowCancelDialog(false);
    setPage(0);

    if (!normalized) {
      setValidationError(
        mode === "REFERENCE"
          ? "Enter your booking reference."
          : "Enter the passenger email address.",
      );
      return;
    }

    if (mode === "REFERENCE") {
      const normalizedReference =
        normalized.toUpperCase();

      if (
        !BOOKING_REFERENCE_PATTERN.test(
          normalizedReference,
        )
      ) {
        setValidationError(
          "Enter a valid booking reference, such as LSF-26-ABC123.",
        );
        return;
      }

      // setReference(normalizedReference);
      setSelectedReference(normalizedReference);
      setEmail("");

      setSearchParams({
        reference: normalizedReference,
      });

      return;
    }

    const normalizedEmail =
      normalized.toLowerCase();

    if (!EMAIL_PATTERN.test(normalizedEmail)) {
      setValidationError(
        "Enter a valid passenger email address.",
      );
      return;
    }

    setEmail(normalizedEmail);
    // setReference("");
    setSelectedReference("");

    setSearchParams({
      email: normalizedEmail,
    });
  }

  function handleSelectBooking(
    booking: BookingSearchItem,
  ) {
    setSelectedReference(booking.reference);
  }

  function handleCancelBooking() {
    setCancellationError(null);

    cancelMutation.mutate(undefined, {
      onSuccess: async () => {
        setShowCancelDialog(false);

        await bookingQuery.refetch();

        if (email) {
          await emailSearchQuery.refetch();
        }
      },

      onError: error => {
        const apiError = getApiError(error);

        setCancellationError(
          apiError?.message ??
            "The booking could not be cancelled.",
        );

        setShowCancelDialog(false);
      },
    });
  }

  const emailResults =
    emailSearchQuery.data?.items ?? [];

  return (
    <PageContainer>
      <header className="manage-booking-hero">
        <span className="manage-booking-hero__badge">
          Booking management
        </span>

        <h1>Manage your bookings</h1>

        <p>
          Search using a booking reference or the
          passenger email address used during booking.
        </p>
      </header>

      <section className="manage-booking-search-card">
        <div className="booking-search-tabs">
          <button
            type="button"
            className={
              mode === "REFERENCE"
                ? "booking-search-tab booking-search-tab--active"
                : "booking-search-tab"
            }
            onClick={() =>
              handleModeChange("REFERENCE")
            }
          >
            Booking reference
          </button>

          <button
            type="button"
            className={
              mode === "EMAIL"
                ? "booking-search-tab booking-search-tab--active"
                : "booking-search-tab"
            }
            onClick={() =>
              handleModeChange("EMAIL")
            }
          >
            Passenger email
          </button>
        </div>

        <div className="manage-booking-search-form">
          <div className="form-field">
            <label htmlFor="booking-search">
              {mode === "REFERENCE"
                ? "Booking reference"
                : "Passenger email"}
            </label>

            <input
              id="booking-search"
              type={
                mode === "EMAIL"
                  ? "email"
                  : "text"
              }
              value={input}
              placeholder={
                mode === "REFERENCE"
                  ? "LSF-26-ABC123"
                  : "passenger@example.com"
              }
              onChange={event => {
                setInput(
                  mode === "REFERENCE"
                    ? event.target.value.toUpperCase()
                    : event.target.value,
                );

                setValidationError(null);
              }}
              onKeyDown={event => {
                if (event.key === "Enter") {
                  handleSearch();
                }
              }}
            />

            {validationError && (
              <span
                className="form-field__error"
                role="alert"
              >
                {validationError}
              </span>
            )}
          </div>

          <button
            type="button"
            className="button button--primary"
            onClick={handleSearch}
          >
            Search bookings
          </button>
        </div>
      </section>

      {mode === "EMAIL" &&
        emailSearchQuery.isLoading && (
          <LoadingSpinner label="Searching bookings..." />
        )}

      {mode === "EMAIL" &&
        emailSearchQuery.isError && (
          <ErrorMessage message="Bookings could not be searched. Please try again." />
        )}

      {mode === "EMAIL" &&
        email &&
        !emailSearchQuery.isLoading &&
        emailResults.length === 0 && (
          <EmptyState
            title="No bookings found"
            message="No bookings were found for this passenger email address."
          />
        )}

      {mode === "EMAIL" &&
        emailResults.length > 0 && (
          <section className="email-booking-results">
            <div className="email-booking-results__header">
              <div>
                <span>Search results</span>
                <h2>
                  {emailSearchQuery.data
                    ?.totalElements ?? 0}{" "}
                  booking
                  {(emailSearchQuery.data
                    ?.totalElements ?? 0) === 1
                    ? ""
                    : "s"}{" "}
                  found
                </h2>
              </div>
            </div>

            <div className="email-booking-results__list">
              {emailResults.map(booking => (
                <article
                  key={booking.reference}
                  className="email-booking-card"
                >
                  <div className="email-booking-card__header">
                    <div>
                      <span>
                        {booking.reference}
                      </span>

                      <h3>
                        {booking.originName}
                        {" → "}
                        {booking.destinationName}
                      </h3>
                    </div>

                    <span
                      className={[
                        "booking-status-badge",
                        `booking-status-badge--${booking.status.toLowerCase()}`,
                      ].join(" ")}
                    >
                      {booking.status}
                    </span>
                  </div>

                  <dl className="email-booking-card__details">
                    <div>
                      <dt>Departure</dt>
                      <dd>
                        {formatDateTime(
                          booking.departureTime,
                        )}
                      </dd>
                    </div>

                    <div>
                      <dt>Seat</dt>
                      <dd>
                        Coach {booking.coachNumber},
                        Seat {booking.seatNumber}
                      </dd>
                    </div>

                    <div>
                      <dt>Fare</dt>
                      <dd>
                        {formatCurrency(
                          booking.fareAmount,
                          booking.currency,
                        )}
                      </dd>
                    </div>
                  </dl>

                  <button
                    type="button"
                    className="button button--secondary button--full"
                    onClick={() =>
                      handleSelectBooking(booking)
                    }
                  >
                    View and manage booking
                  </button>
                </article>
              ))}
            </div>

            {(emailSearchQuery.data?.totalPages ??
              0) > 1 && (
              <div className="booking-pagination">
                <button
                  type="button"
                  className="button button--secondary"
                  disabled={page === 0}
                  onClick={() =>
                    setPage(current =>
                      Math.max(current - 1, 0),
                    )
                  }
                >
                  Previous
                </button>

                <span>
                  Page {page + 1} of{" "}
                  {
                    emailSearchQuery.data
                      ?.totalPages
                  }
                </span>

                <button
                  type="button"
                  className="button button--secondary"
                  disabled={
                    page + 1 >=
                    (emailSearchQuery.data
                      ?.totalPages ?? 0)
                  }
                  onClick={() =>
                    setPage(current => current + 1)
                  }
                >
                  Next
                </button>
              </div>
            )}
          </section>
        )}

      {bookingQuery.isLoading && (
        <LoadingSpinner label="Loading booking details..." />
      )}

      {bookingQuery.isError &&
        selectedReference && (
          <ErrorMessage message="The selected booking could not be loaded." />
        )}

      {cancellationError && (
        <ErrorMessage
          message={cancellationError}
        />
      )}

      {bookingQuery.data && (
        <section className="selected-booking-result">
          <BookingConfirmation
            booking={bookingQuery.data}
          />

          {bookingQuery.data.status ===
            "CONFIRMED" && (
            <section className="manage-booking-cancellation">
              <div>
                <h2>Cancel this booking</h2>

                <p>
                  Cancelling releases the seat for
                  another passenger.
                </p>
              </div>

              <button
                type="button"
                className="button button--danger"
                onClick={() =>
                  setShowCancelDialog(true)
                }
              >
                Cancel booking
              </button>
            </section>
          )}
        </section>
      )}

      {showCancelDialog &&
        bookingQuery.data && (
          <div className="modal-backdrop">
            <section
              className="confirmation-dialog"
              role="alertdialog"
              aria-modal="true"
            >
              <div className="confirmation-dialog__icon">
                !
              </div>

              <h2>Cancel this booking?</h2>

              <p>
                Booking{" "}
                <strong>
                  {
                    bookingQuery.data
                      .reference
                  }
                </strong>{" "}
                will be cancelled.
              </p>

              <div className="confirmation-dialog__actions">
                <button
                  type="button"
                  className="button button--secondary"
                  onClick={() =>
                    setShowCancelDialog(false)
                  }
                >
                  Keep booking
                </button>

                <button
                  type="button"
                  className="button button--danger"
                  disabled={
                    cancelMutation.isPending
                  }
                  onClick={handleCancelBooking}
                >
                  {cancelMutation.isPending
                    ? "Cancelling..."
                    : "Yes, cancel booking"}
                </button>
              </div>
            </section>
          </div>
        )}
    </PageContainer>
  );
}