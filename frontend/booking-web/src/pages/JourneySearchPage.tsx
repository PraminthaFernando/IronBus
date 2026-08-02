import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

import { findJourneys } from "../api/journeys-api";
import { getRoutes, getRouteStations } from "../api/routes-api";
import { queryKeys } from "../lib/query-keys";
import { formatDateTime } from "../lib/date-time";

import { PageContainer } from "../components/common/PageContainer";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { ErrorMessage } from "../components/common/ErrorMessage";
import { EmptyState } from "../components/common/EmptyState";

interface SearchCriteria {
  routeId: string;
  originStationId: string;
  destinationStationId: string;
  date: string;
}

export function JourneySearchPage() {
  const navigate = useNavigate();

  const [routeId, setRouteId] = useState("");
  const [originStationId, setOriginStationId] = useState("");
  const [destinationStationId, setDestinationStationId] = useState("");
  const [date, setDate] = useState("");
  const [searchCriteria, setSearchCriteria] = useState<SearchCriteria | null>(
    null,
  );
  const [validationError, setValidationError] = useState<string | null>(null);

  const routesQuery = useQuery({
    queryKey: queryKeys.routes,
    queryFn: getRoutes,
  });

  const routeStationsQuery = useQuery({
    queryKey: routeId
      ? queryKeys.routeStations(routeId)
      : ["route-stations", "disabled"],
    queryFn: () => getRouteStations(routeId),
    enabled: Boolean(routeId),
  });

  const journeysQuery = useQuery({
    queryKey: searchCriteria
      ? queryKeys.journeys(searchCriteria.routeId, searchCriteria.date)
      : ["journeys", "disabled"],

    queryFn: () =>
      findJourneys({
        routeId: searchCriteria!.routeId,
        date: searchCriteria!.date,
      }),

    enabled: Boolean(searchCriteria),
  });

  const stations = routeStationsQuery.data?.stations ?? [];

  const selectedOrigin = useMemo(
    () => stations.find((station) => station.stationId === originStationId),
    [stations, originStationId],
  );

  const availableDestinations = useMemo(() => {
    if (!selectedOrigin) {
      return [];
    }

    return stations.filter(
      (station) => station.sequenceNumber > selectedOrigin.sequenceNumber,
    );
  }, [stations, selectedOrigin]);

  const canSearch =
    Boolean(routeId) &&
    Boolean(originStationId) &&
    Boolean(destinationStationId) &&
    Boolean(date) &&
    !journeysQuery.isFetching;

  function clearSearchResults() {
    setSearchCriteria(null);
    setValidationError(null);
  }

  function handleRouteChange(value: string) {
    setRouteId(value);
    setOriginStationId("");
    setDestinationStationId("");
    clearSearchResults();
  }

  function handleOriginChange(value: string) {
    setOriginStationId(value);
    setDestinationStationId("");
    clearSearchResults();
  }

  function handleSearch() {
    setValidationError(null);

    if (!routeId) {
      setValidationError("Please select a route.");
      return;
    }

    if (!originStationId) {
      setValidationError("Please select your origin station.");
      return;
    }

    if (!destinationStationId) {
      setValidationError("Please select your destination station.");
      return;
    }

    if (!date) {
      setValidationError("Please select a travel date.");
      return;
    }

    if (originStationId === destinationStationId) {
      setValidationError("Origin and destination must be different.");
      return;
    }

    const destination = stations.find(
      (station) => station.stationId === destinationStationId,
    );

    if (
      !selectedOrigin ||
      !destination ||
      destination.sequenceNumber <= selectedOrigin.sequenceNumber
    ) {
      setValidationError(
        "Destination must appear after the origin on the selected route.",
      );
      return;
    }

    setSearchCriteria({
      routeId,
      originStationId,
      destinationStationId,
      date,
    });
  }

  function handleSelectJourney(journeyId: string) {
    if (!searchCriteria) {
      return;
    }

    const params = new URLSearchParams({
      originStationId: searchCriteria.originStationId,
      destinationStationId: searchCriteria.destinationStationId,
    });

    navigate(`/journeys/${journeyId}/seats?${params.toString()}`);
  }

  const today = new Date().toISOString().split("T")[0];

  return (
    <PageContainer>
      <section className="journey-search-hero">
        <span className="journey-search-hero__badge">Reserved train seats</span>

        <h1 className="journey-search-hero__title">Find your journey</h1>

        <p className="journey-search-hero__description">
          Choose your route, travel leg, and date to discover available trains
          and reserved seats.
        </p>
      </section>

      <section className="journey-search-card">
        <div className="journey-search-card__header">
          <div>
            <h2>Journey details</h2>
            <p>Enter your travel information to search scheduled trains.</p>
          </div>
        </div>

        <div className="journey-search-form">
          <div className="form-field">
            <label htmlFor="route">Route</label>

            <select
              id="route"
              value={routeId}
              onChange={(event) => handleRouteChange(event.target.value)}
              disabled={routesQuery.isLoading}
            >
              <option value="">Select a route</option>

              {routesQuery.data?.map((route) => (
                <option key={route.id} value={route.id}>
                  {route.name} ({route.code})
                </option>
              ))}
            </select>

            <span className="form-field__hint">Choose the railway route.</span>
          </div>

          <div className="form-field">
            <label htmlFor="origin">Origin</label>

            <select
              id="origin"
              value={originStationId}
              onChange={(event) => handleOriginChange(event.target.value)}
              disabled={!routeId || routeStationsQuery.isLoading}
            >
              <option value="">Select origin</option>

              {stations.map((station) => (
                <option key={station.stationId} value={station.stationId}>
                  {station.stationName}
                </option>
              ))}
            </select>

            <span className="form-field__hint">Your boarding station.</span>
          </div>

          <div className="form-field">
            <label htmlFor="destination">Destination</label>

            <select
              id="destination"
              value={destinationStationId}
              onChange={(event) => {
                setDestinationStationId(event.target.value);
                clearSearchResults();
              }}
              disabled={!originStationId}
            >
              <option value="">Select destination</option>

              {availableDestinations.map((station) => (
                <option key={station.stationId} value={station.stationId}>
                  {station.stationName}
                </option>
              ))}
            </select>

            <span className="form-field__hint">
              Only valid forward stations are shown.
            </span>
          </div>

          <div className="form-field">
            <label htmlFor="travel-date">Travel date</label>

            <input
              id="travel-date"
              type="date"
              min={today}
              value={date}
              onChange={(event) => {
                setDate(event.target.value);
                clearSearchResults();
              }}
            />

            <span className="form-field__hint">
              Select today or a future date.
            </span>
          </div>
        </div>

        {validationError && <ErrorMessage message={validationError} />}

        {routesQuery.isError && (
          <ErrorMessage message="Routes could not be loaded. Please check the backend connection and try again." />
        )}

        {routeStationsQuery.isError && (
          <ErrorMessage message="Stations for the selected route could not be loaded." />
        )}

        <div className="journey-search-card__actions">
          <button
            type="button"
            className="button button--primary"
            disabled={!canSearch}
            onClick={handleSearch}
          >
            {journeysQuery.isFetching
              ? "Searching journeys..."
              : "Search journeys"}
          </button>
        </div>
      </section>

      {journeysQuery.isLoading && (
        <LoadingSpinner label="Searching journeys..." />
      )}

      {journeysQuery.isError && (
        <ErrorMessage message="Journeys could not be loaded. Please try again." />
      )}

      {journeysQuery.data?.length === 0 && (
        <EmptyState
          title="No journeys found"
          message="No scheduled journeys are available for the selected route and date."
          action={
            <button
              type="button"
              className="button button--secondary"
              onClick={() => setSearchCriteria(null)}
            >
              Change search
            </button>
          }
        />
      )}

      {journeysQuery.data && journeysQuery.data.length > 0 && (
        <section className="journey-results">
          <div className="journey-results__header">
            <div>
              <span className="journey-results__eyebrow">Search results</span>

              <h2>Available journeys</h2>
            </div>

            <span className="journey-results__count">
              {journeysQuery.data.length} found
            </span>
          </div>

          <div className="journey-results__grid">
            {journeysQuery.data.map((journey) => (
              <article className="journey-result-card" key={journey.id}>
                <div className="journey-result-card__top">
                  <div>
                    <span className="journey-result-card__code">
                      {journey.trainCode}
                    </span>

                    <h3>{journey.trainName}</h3>

                    <p>{journey.routeName}</p>
                  </div>

                  <span className="journey-status">{journey.status}</span>
                </div>

                <div className="journey-result-card__details">
                  <div>
                    <span>Departure</span>
                    <strong>{formatDateTime(journey.departureTime)}</strong>
                  </div>
                </div>

                <button
                  type="button"
                  className="button button--primary button--full"
                  onClick={() => handleSelectJourney(journey.id)}
                >
                  View available seats
                </button>
              </article>
            ))}
          </div>
        </section>
      )}
    </PageContainer>
  );
}
