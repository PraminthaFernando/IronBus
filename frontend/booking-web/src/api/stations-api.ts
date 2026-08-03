import type { Station } from "../types/domain";
import { apiClient } from "./api-client";

export async function getStations(): Promise<Station[]> {
  return (await apiClient.get<Station[]>("v1/stations")).data;
}
