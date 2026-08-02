import type { RouteSummary, RouteWithStations } from "../types/domain";
import { apiClient } from "./api-client";

export async function getRoutes() {
  const response =
    await apiClient.get<RouteSummary[]>("/routes");

  return response.data;
}

export async function getRouteStations(
  routeId: string,
) {
    const response =
        await apiClient.get<RouteWithStations>(
        `/routes/${routeId}/stations`,
        );

    console.log("getRouteStations response:", response.data);    

    return response.data;
}