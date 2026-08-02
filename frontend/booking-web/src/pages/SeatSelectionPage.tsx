import { useMemo, useState } from "react";
import {
  useNavigate,
  useParams,
  useSearchParams,
} from "react-router-dom";

import { PageContainer } from "../components/common/PageContainer";
import { AvailableSeatList } from "../components/seat/AvailableSeatList";
import { EmptyState } from "../components/common/EmptyState";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { SeatFilters } from "../components/seat/SeatFilters";
import type {
  SeatType,
  TravelClass,
} from "../types/domain";
import { formatCurrency } from "../lib/currency";
import { useAvailableSeats } from "../hooks/use-available-seats";

interface SeatFilterState {
  travelClass: TravelClass | "ALL";
  seatType: SeatType | "ALL";
}

export function SeatSelectionPage() {
  const navigate = useNavigate();
  const { journeyId } = useParams();
  const [searchParams] = useSearchParams();

  const originStationId =
    searchParams.get("originStationId") ?? "";

  const destinationStationId =
    searchParams.get("destinationStationId") ?? "";

  const [selectedSeatId, setSelectedSeatId] =
    useState<string | null>(null);

  const [filters, setFilters] =
    useState<SeatFilterState>({
      travelClass: "ALL",
      seatType: "ALL",
    });

  const availabilityQuery = useAvailableSeats({
    journeyId,
    originStationId,
    destinationStationId,
  });

  const availableSeats =
    availabilityQuery.data?.seats ?? [];

  const filteredSeats = useMemo(() => {
    return availableSeats.filter(seat => {
      const classMatches =
        filters.travelClass === "ALL" ||
        seat.travelClass === filters.travelClass;

      const typeMatches =
        filters.seatType === "ALL" ||
        seat.seatType === filters.seatType;

      return classMatches && typeMatches;
    });
  }, [availableSeats, filters]);

  const selectedSeat = useMemo(
    () =>
      availableSeats.find(
        seat => seat.seatId === selectedSeatId,
      ) ?? null,
    [availableSeats, selectedSeatId],
  );

  function handleContinue() {
    if (
      !journeyId ||
      !selectedSeat ||
      !originStationId ||
      !destinationStationId
    ) {
      return;
    }

    const params = new URLSearchParams({
      originStationId,
      destinationStationId,
      seatId: selectedSeat.seatId,
    });

    navigate(
      `/journeys/${journeyId}/book?${params.toString()}`,
    );
  }

  function handleBack() {
    navigate(-1);
  }

  if (
    !journeyId ||
    !originStationId ||
    !destinationStationId
  ) {
    return (
      <PageContainer>
        <ErrorMessage message="Journey information is incomplete. Please return to the journey search page and try again." />

        <button
          type="button"
          className="button button--secondary"
          onClick={() =>
            navigate("/journeys/search")
          }
        >
          Back to journey search
        </button>
      </PageContainer>
    );
  }

  if (availabilityQuery.isLoading) {
    return (
      <PageContainer>
        <LoadingSpinner label="Checking available seats..." />
      </PageContainer>
    );
  }

  if (availabilityQuery.isError) {
    return (
      <PageContainer>
        <ErrorMessage message="Seat availability could not be loaded. Please try again." />

        <div className="seat-selection-actions">
          <button
            type="button"
            className="button button--secondary"
            onClick={handleBack}
          >
            Back
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
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <header className="seat-selection-hero">
        <span className="seat-selection-hero__badge">
          Reserved seating
        </span>

        <h1>Select your seat</h1>

        <p>
          Choose one available seat for your selected
          journey leg.
        </p>
      </header>

      {availabilityQuery.data && (
        <section className="seat-selection-summary">
          <div>
            <span>Travel distance</span>
            <strong>
              {availabilityQuery.data.distanceKm} km
            </strong>
          </div>

          <div>
            <span>Route segments</span>
            <strong>
              {
                availabilityQuery.data
                  .segmentSequences.length
              }
            </strong>
          </div>

          <div>
            <span>Available seats</span>
            <strong>
              {availableSeats.length}
            </strong>
          </div>
        </section>
      )}

      <section className="seat-selection-toolbar">
        <div>
          <h2>Available seats</h2>
          <p>
            Filter by travel class or seat type.
          </p>
        </div>

        <button
          type="button"
          className="button button--secondary"
          onClick={() =>
            availabilityQuery.refetch()
          }
          disabled={
            availabilityQuery.isFetching
          }
        >
          {availabilityQuery.isFetching
            ? "Refreshing..."
            : "Refresh availability"}
        </button>
      </section>

      <SeatFilters
        value={filters}
        onChange={setFilters}
        disabled={availabilityQuery.isFetching}
        />

      {availableSeats.length === 0 ? (
        <EmptyState
          title="No reserved seats available"
          message="There are no available reserved seats for this journey leg. Try another journey or date."
          action={
            <button
              type="button"
              className="button button--secondary"
              onClick={handleBack}
            >
              Choose another journey
            </button>
          }
        />
      ) : filteredSeats.length === 0 ? (
        <EmptyState
          title="No seats match these filters"
          message="Try selecting another travel class or seat type."
          action={
            <button
              type="button"
              className="button button--secondary"
              onClick={() =>
                setFilters({
                  travelClass: "ALL",
                  seatType: "ALL",
                })
              }
            >
              Clear filters
            </button>
          }
        />
      ) : (
        <AvailableSeatList
          seats={filteredSeats}
          selectedSeatId={selectedSeatId}
          onSelect={setSelectedSeatId}
        />
      )}

      <aside className="selected-seat-panel">
        <div>
          <span className="selected-seat-panel__label">
            Selected seat
          </span>

          {selectedSeat ? (
            <>
              <h2>
                Coach {selectedSeat.coachNumber} · Seat{" "}
                {selectedSeat.seatNumber}
              </h2>

              <p>
                {selectedSeat.travelClass.replaceAll(
                  "_",
                  " ",
                )}
                {" · "}
                {selectedSeat.seatType}
              </p>
            </>
          ) : (
            <>
              <h2>No seat selected</h2>
              <p>
                Select one seat above to continue.
              </p>
            </>
          )}
        </div>

        <div className="selected-seat-panel__fare">
          {selectedSeat && (
            <>
              <span>Fare</span>
              <strong>
                {formatCurrency(
                  selectedSeat.fareAmount,
                  selectedSeat.currency,
                )}
              </strong>
            </>
          )}
        </div>

        <div className="selected-seat-panel__actions">
          <button
            type="button"
            className="button button--secondary"
            onClick={handleBack}
          >
            Back
          </button>

          <button
            type="button"
            className="button button--primary"
            disabled={!selectedSeat}
            onClick={handleContinue}
          >
            Continue to passenger details
          </button>
        </div>
      </aside>

      <p className="availability-note">
        Availability is a live snapshot. Your seat is
        confirmed only after the booking is completed.
      </p>
    </PageContainer>
  );
}