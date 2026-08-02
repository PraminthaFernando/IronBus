import { useMemo, useState } from "react";
import {
  useNavigate,
  useParams,
  useSearchParams,
} from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";

import { useAvailableSeats } from "../hooks/use-available-seats";
import { useCreateBooking } from "../hooks/use-booking";

import { PageContainer } from "../components/common/PageContainer";
import { PassengerForm } from "../components/booking/PassengerForm";
import { BookingConflictDialog } from "../components/booking/BookingConflictDialog";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { ErrorMessage } from "../components/common/ErrorMessage";

import { getApiError } from "../api/error-utils";
import { queryKeys } from "../lib/query-keys";
import { formatCurrency } from "../lib/currency";

import type { PassengerFormValues } from "../schemas/passenger-schema";
import type { BookingResponse } from "../types/domain";

export function BookingDetailsPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { journeyId = "" } = useParams();
  const [searchParams] = useSearchParams();

  const originStationId =
    searchParams.get("originStationId") ?? "";

  const destinationStationId =
    searchParams.get("destinationStationId") ?? "";

  const seatId =
    searchParams.get("seatId") ?? "";

  const [conflictOpen, setConflictOpen] =
    useState(false);

  const [submissionError, setSubmissionError] =
    useState<string | null>(null);

  const [passengerDraft, setPassengerDraft] =
    useState<PassengerFormValues | undefined>();

  const availabilityQuery = useAvailableSeats({
    journeyId,
    originStationId,
    destinationStationId,
  });

  const selectedSeat = useMemo(
    () =>
      availabilityQuery.data?.seats.find(
        seat => seat.seatId === seatId,
      ) ?? null,
    [availabilityQuery.data, seatId],
  );

  const createBookingMutation =
    useCreateBooking(
      journeyId,
      originStationId,
      destinationStationId,
    );

  const hasValidRouteInformation =
    Boolean(journeyId) &&
    Boolean(originStationId) &&
    Boolean(destinationStationId) &&
    Boolean(seatId);

  async function handleSubmit(
    passenger: PassengerFormValues,
  ) {
    setPassengerDraft(passenger);
    setSubmissionError(null);

    if (!selectedSeat) {
      setSubmissionError(
        "The selected seat is no longer available. Please choose another seat.",
      );
      return;
    }

    createBookingMutation.mutate(
      {
        journeyId,
        seatId: selectedSeat.seatId,
        originStationId,
        destinationStationId,
        passenger,
      },
      {
        onSuccess: (
          booking: BookingResponse,
        ) => {
          navigate(
            `/bookings/${booking.reference}/confirmation`,
          );
        },

        onError: async error => {
          const apiError = getApiError(error);

          if (
            apiError?.status === 409 &&
            apiError.code ===
              "SEAT_SEGMENT_CONFLICT"
          ) {
            await queryClient.invalidateQueries({
              queryKey:
                queryKeys.availableSeats(
                  journeyId,
                  originStationId,
                  destinationStationId,
                ),
            });

            setConflictOpen(true);
            return;
          }

          setSubmissionError(
            apiError?.message ??
              "The booking could not be completed. Please try again.",
          );
        },
      },
    );
  }

  function handleBackToSeats() {
    const params = new URLSearchParams({
      originStationId,
      destinationStationId,
    });

    navigate(
      `/journeys/${journeyId}/seats?${params.toString()}`,
      {
        state: {
          passengerDraft,
        },
      },
    );
  }

  if (!hasValidRouteInformation) {
    return (
      <PageContainer>
        <section className="booking-error-state">
          <ErrorMessage message="The booking information is incomplete. Please return to seat selection and try again." />

          <button
            type="button"
            className="button button--primary"
            onClick={() =>
              navigate("/journeys/search")
            }
          >
            Return to journey search
          </button>
        </section>
      </PageContainer>
    );
  }

  if (availabilityQuery.isLoading) {
    return (
      <PageContainer>
        <LoadingSpinner label="Validating your selected seat..." />
      </PageContainer>
    );
  }

  if (availabilityQuery.isError) {
    return (
      <PageContainer>
        <section className="booking-error-state">
          <ErrorMessage message="The selected seat could not be validated. Please try again." />

          <div className="booking-error-state__actions">
            <button
              type="button"
              className="button button--secondary"
              onClick={handleBackToSeats}
            >
              Back to seats
            </button>

            <button
              type="button"
              className="button button--primary"
              onClick={() =>
                availabilityQuery.refetch()
              }
            >
              Retry
            </button>
          </div>
        </section>
      </PageContainer>
    );
  }

  if (!selectedSeat) {
    return (
      <PageContainer>
        <section className="booking-error-state">
          <ErrorMessage message="The selected seat is no longer available." />

          <button
            type="button"
            className="button button--primary"
            onClick={handleBackToSeats}
          >
            Select another seat
          </button>
        </section>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <header className="booking-review-hero">
        <span className="booking-review-hero__badge">
          Final step
        </span>

        <h1>Review your booking</h1>

        <p>
          Confirm your seat and enter the passenger
          details required to complete the booking.
        </p>
      </header>

      <div className="booking-review-layout">
        <section className="booking-review-main">
          <div className="booking-section-card">
            <div className="booking-section-card__header">
              <div>
                <span className="booking-section-card__step">
                  1
                </span>

                <div>
                  <h2>Passenger details</h2>

                  <p>
                    Enter the information for the
                    passenger using this seat.
                  </p>
                </div>
              </div>
            </div>

            {submissionError && (
              <ErrorMessage
                message={submissionError}
              />
            )}

            <PassengerForm
              submitting={
                createBookingMutation.isPending
              }
              onSubmit={handleSubmit}
            />
          </div>
        </section>

        <aside className="booking-summary-card">
          <div className="booking-summary-card__header">
            <span>Booking summary</span>
            <h2>Your selected seat</h2>
          </div>

          <div className="booking-summary-seat">
            <div className="booking-summary-seat__icon">
              {selectedSeat.seatNumber}
            </div>

            <div>
              <strong>
                Coach {selectedSeat.coachNumber}
              </strong>

              <span>
                Seat {selectedSeat.seatNumber}
              </span>
            </div>
          </div>

          <dl className="booking-summary-details">
            <div>
              <dt>Travel class</dt>
              <dd>
                {selectedSeat.travelClass
                  .replaceAll("_", " ")
                  .toLowerCase()
                  .replace(/\b\w/g, value =>
                    value.toUpperCase(),
                  )}
              </dd>
            </div>

            <div>
              <dt>Seat type</dt>
              <dd>
                {selectedSeat.seatType
                  .toLowerCase()
                  .replace(/\b\w/g, value =>
                    value.toUpperCase(),
                  )}
              </dd>
            </div>

            {availabilityQuery.data && (
              <>
                <div>
                  <dt>Travel distance</dt>
                  <dd>
                    {
                      availabilityQuery.data
                        .distanceKm
                    }{" "}
                    km
                  </dd>
                </div>

                <div>
                  <dt>Route segments</dt>
                  <dd>
                    {
                      availabilityQuery.data
                        .segmentSequences.length
                    }
                  </dd>
                </div>
              </>
            )}
          </dl>

          <div className="booking-summary-fare">
            <span>Total fare</span>

            <strong>
              {formatCurrency(
                selectedSeat.fareAmount,
                selectedSeat.currency,
              )}
            </strong>
          </div>

          <p className="booking-summary-note">
            The fare is calculated by the server based
            on the selected journey leg and travel
            class.
          </p>

          <button
            type="button"
            className="button button--secondary button--full"
            disabled={
              createBookingMutation.isPending
            }
            onClick={handleBackToSeats}
          >
            Change seat
          </button>
        </aside>
      </div>

      <section className="booking-security-note">
        <div>
          <strong>
            Your seat is not confirmed yet
          </strong>

          <p>
            The final reservation is created only
            after you submit this form. Another
            passenger may reserve the same seat before
            your request is completed.
          </p>
        </div>
      </section>

      <BookingConflictDialog
        open={conflictOpen}
        onClose={() => {
          setConflictOpen(false);
          handleBackToSeats();
        }}
      />
    </PageContainer>
  );
}