import { useQuery } from "@tanstack/react-query";
import { queryKeys } from "../lib/query-keys";
import { findAvailableSeats } from "../api/availability-api";

export function useAvailableSeats(p: {
  journeyId?: string;
  originStationId?: string;
  destinationStationId?: string;
}) {
  const e = !!(p.journeyId && p.originStationId && p.destinationStationId);
  return useQuery({
    queryKey: e
      ? queryKeys.availableSeats(
          p.journeyId!,
          p.originStationId!,
          p.destinationStationId!,
        )
      : ["available-seats", "disabled"],
    queryFn: () =>
      findAvailableSeats({
        journeyId: p.journeyId!,
        originStationId: p.originStationId!,
        destinationStationId: p.destinationStationId!,
      }),
    enabled: e,
    staleTime: 0,
    refetchOnWindowFocus: true,
  });
}
