export const queryKeys = {
  routes: ["routes"] as const,

  stations: ["stations"] as const,

  routeStations: (routeId: string) => ["route-stations", routeId] as const,

  journeys: (routeId: string, date: string) =>
    ["journeys", routeId, date] as const,

  availableSeats: (
    journeyId: string,
    originStationId: string,
    destinationStationId: string,
  ) =>
    [
      "available-seats",
      journeyId,
      originStationId,
      destinationStationId,
    ] as const,

  booking: (reference: string) => ["booking", reference] as const,
};
