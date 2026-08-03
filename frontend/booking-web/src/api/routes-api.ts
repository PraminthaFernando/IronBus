import type { RouteSummary, RouteWithStations } from "../types/domain";
import { apiClient } from "./api-client";

export async function getRoutes() {
  const response =
    await apiClient.get<RouteSummary[]>("/v1/routes");

  return response.data;
}

export async function getRouteStations(
  routeId: string,
) {
    const response =
        await apiClient.get<RouteWithStations>(
        `/v1/routes/${routeId}/stations`,
        );

    console.log("getRouteStations response:", response.data);    

    return response.data;
}