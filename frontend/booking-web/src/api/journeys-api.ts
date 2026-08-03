import type { JourneySummary } from "../types/domain";
import { apiClient } from "./api-client";

export interface FindJourneysParams {
  routeId: string;
  date: string;
}
export async function findJourneys(
  params: FindJourneysParams,
): Promise<JourneySummary[]> {
  return (await apiClient.get<JourneySummary[]>("v1/journeys", { params })).data;
}
