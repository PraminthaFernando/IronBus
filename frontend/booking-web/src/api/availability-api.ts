import type { AvailabilityResponse } from "../types/domain";
import { apiClient } from "./api-client";

export interface FindAvailableSeatsParams {
  journeyId: string;
  originStationId: string;
  destinationStationId: string;
}
export async function findAvailableSeats(
  p: FindAvailableSeatsParams,
): Promise<AvailabilityResponse> {
  return (
    await apiClient.get<AvailabilityResponse>(
      `/v1/journeys/${p.journeyId}/available-seats`,
      {
        params: {
          originStationId: p.originStationId,
          destinationStationId: p.destinationStationId,
        },
      },
    )
  ).data;
}