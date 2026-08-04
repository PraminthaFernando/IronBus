import { apiClient } from "../../api/api-client";
import type {
  AdminDashboardSummary,
  CoachAdminSummary,
  JourneyAdminSummary,
  JourneyListQuery,
  ListQuery,
  PageResponse,
  RouteAdminSummary,
  RouteStationAdminItem,
  SeatAdminSummary,
  StationAdminSummary,
  TrainAdminSummary,
} from "../types/admin";
import type {
  BulkSeatFormValues,
  CoachFormValues,
  JourneyFormValues,
  RouteFormValues,
  SeatFormValues,
  StationFormValues,
  TrainFormValues,
} from "../schemas/forms";

const q = (o: Record<string, unknown>) => {
  const p = new URLSearchParams();
  Object.entries(o).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== "") p.set(k, String(v));
  });
  return p;
};

export const adminApi = {
  dashboard: async () =>
    (await apiClient.get<AdminDashboardSummary>("/v1/admin/dashboard")).data,
  stations: {
    list: async (x: ListQuery) =>
      (
        await apiClient.get<PageResponse<StationAdminSummary>>(
          `/v1/admin/stations?${q({ ...x, page: x.page ?? 0, size: x.size ?? 20 })}`,
        )
      ).data,
    get: async (id: string) =>
      (await apiClient.get<StationAdminSummary>(`/v1/admin/stations/${id}`))
        .data,
    create: async (b: StationFormValues) =>
      (await apiClient.post<StationAdminSummary>("/v1/admin/stations", b))
        .data,
    update: async (
      id: string,
      b: StationFormValues & { expectedVersion: number },
    ) =>
      (await apiClient.put<StationAdminSummary>(`/v1/admin/stations/${id}`, b))
        .data,
  },
  routes: {
    list: async (x: ListQuery) =>
      (
        await apiClient.get<PageResponse<RouteAdminSummary>>(
          `/v1/admin/routes?${q({ ...x, page: x.page ?? 0, size: x.size ?? 20 })}`,
        )
      ).data,
    get: async (id: string) =>
      (await apiClient.get<RouteAdminSummary>(`/v1/admin/routes/${id}`)).data,
    create: async (b: RouteFormValues) =>
      (await apiClient.post<RouteAdminSummary>("/v1/admin/routes", b)).data,
    update: async (
      id: string,
      b: RouteFormValues & { expectedVersion: number },
    ) =>
      (await apiClient.put<RouteAdminSummary>(`/v1/admin/routes/${id}`, b))
        .data,
    stations: async (id: string) =>
      (
        await apiClient.get<RouteStationAdminItem[]>(
          `/v1/admin/routes/${id}/stations`,
        )
      ).data,
    saveStations: async (
      id: string,
      b: { stations: RouteStationAdminItem[]; expectedVersion: number },
    ) =>
      (
        await apiClient.put<RouteStationAdminItem[]>(
          `/v1/admin/routes/${id}/stations`,
          b,
        )
      ).data,
  },
  trains: {
    list: async (x: ListQuery) =>
      (
        await apiClient.get<PageResponse<TrainAdminSummary>>(
          `/v1/admin/trains?${q({ ...x, page: x.page ?? 0, size: x.size ?? 20 })}`,
        )
      ).data,
    get: async (id: string) =>
      (await apiClient.get<TrainAdminSummary>(`/v1/admin/trains/${id}`)).data,
    create: async (b: TrainFormValues) =>
      (await apiClient.post<TrainAdminSummary>("/v1/admin/trains", b)).data,
    update: async (
      id: string,
      b: TrainFormValues & { expectedVersion: number },
    ) =>
      (await apiClient.put<TrainAdminSummary>(`/v1/admin/trains/${id}`, b))
        .data,
  },
  coaches: {
    list: async (trainId: string, x: ListQuery) =>
      (
        await apiClient.get<PageResponse<CoachAdminSummary>>(
          `/v1/admin/trains/${trainId}/coaches?${q({ ...x, page: x.page ?? 0, size: x.size ?? 20 })}`,
        )
      ).data,
    get: async (id: string) =>
      (await apiClient.get<CoachAdminSummary>(`/v1/admin/coaches/${id}`)).data,
    create: async (trainId: string, b: CoachFormValues) =>
      (
        await apiClient.post<CoachAdminSummary>(
          `/v1/admin/trains/${trainId}/coaches`,
          b,
        )
      ).data,
    update: async (
      id: string,
      b: CoachFormValues & { expectedVersion: number },
    ) =>
      (await apiClient.put<CoachAdminSummary>(`/v1/admin/coaches/${id}`, b))
        .data,
  },
  seats: {
    list: async (coachId: string, x: ListQuery) =>
      (
        await apiClient.get<PageResponse<SeatAdminSummary>>(
          `/v1/admin/coaches/${coachId}/seats?${q({ ...x, page: x.page ?? 0, size: x.size ?? 50 })}`,
        )
      ).data,
    get: async (id: string) =>
      (await apiClient.get<SeatAdminSummary>(`/v1/admin/seats/${id}`)).data,
    create: async (coachId: string, b: SeatFormValues) =>
      (
        await apiClient.post<SeatAdminSummary>(
          `/v1/admin/coaches/${coachId}/seats`,
          b,
        )
      ).data,
    bulk: async (coachId: string, b: BulkSeatFormValues) =>
      (
        await apiClient.post<SeatAdminSummary[]>(
          `/v1/admin/coaches/${coachId}/seats/bulk`,
          b,
        )
      ).data,
    update: async (
      id: string,
      b: SeatFormValues & { expectedVersion: number },
    ) =>
      (await apiClient.put<SeatAdminSummary>(`/v1/admin/seats/${id}`, b)).data,
  },
  journeys: {
    list: async (x: JourneyListQuery) =>
      (
        await apiClient.get<PageResponse<JourneyAdminSummary>>(
          `/v1/admin/journeys?${q({ ...x, page: x.page ?? 0, size: x.size ?? 20 })}`,
        )
      ).data,
    get: async (id: string) =>
      (await apiClient.get<JourneyAdminSummary>(`/v1/admin/journeys/${id}`))
        .data,
    create: async (b: JourneyFormValues) =>
      (await apiClient.post<JourneyAdminSummary>("/v1/admin/journeys", b))
        .data,
    update: async (
      id: string,
      b: JourneyFormValues & { expectedVersion: number },
    ) =>
      (await apiClient.put<JourneyAdminSummary>(`/v1/admin/journeys/${id}`, b))
        .data,
    cancel: async (id: string, expectedVersion: number) =>
      (
        await apiClient.post<JourneyAdminSummary>(
          `/v1/admin/journeys/${id}/cancel`,
          { expectedVersion },
        )
      ).data,
  },
};
