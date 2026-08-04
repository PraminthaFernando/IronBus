import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { adminApi } from "../api/admin-api";
import type {
  JourneyListQuery,
  ListQuery,
} from "../types/admin";
export const keys = {
  dashboard: ["admin", "dashboard"] as const,
  stations: ["admin", "stations"] as const,
  routes: ["admin", "routes"] as const,
  trains: ["admin", "trains"] as const,
  journeys: ["admin", "journeys"] as const,
};
export const useDashboard = () =>
  useQuery({
    queryKey: keys.dashboard,
    queryFn: adminApi.dashboard,
    staleTime: 30000,
  });
export const useStations = (x: ListQuery) =>
  useQuery({
    queryKey: [...keys.stations, x],
    queryFn: () => adminApi.stations.list(x),
  });
export const useStation = (id?: string) =>
  useQuery({
    queryKey: [...keys.stations, id],
    queryFn: () => adminApi.stations.get(id!),
    enabled: !!id,
  });
export const useRoutes = (x: ListQuery) =>
  useQuery({
    queryKey: [...keys.routes, x],
    queryFn: () => adminApi.routes.list(x),
  });
export const useRoute = (id?: string) =>
  useQuery({
    queryKey: [...keys.routes, id],
    queryFn: () => adminApi.routes.get(id!),
    enabled: !!id,
  });
export const useRouteStations = (id?: string) =>
  useQuery({
    queryKey: [...keys.routes, id, "stations"],
    queryFn: () => adminApi.routes.stations(id!),
    enabled: !!id,
  });
export const useTrains = (x: ListQuery) =>
  useQuery({
    queryKey: [...keys.trains, x],
    queryFn: () => adminApi.trains.list(x),
  });
export const useTrain = (id?: string) =>
  useQuery({
    queryKey: [...keys.trains, id],
    queryFn: () => adminApi.trains.get(id!),
    enabled: !!id,
  });
export const useCoaches = (trainId: string, x: ListQuery) =>
  useQuery({
    queryKey: ["admin", "coaches", trainId, x],
    queryFn: () => adminApi.coaches.list(trainId, x),
    enabled: !!trainId,
  });
export const useCoach = (id?: string) =>
  useQuery({
    queryKey: ["admin", "coach", id],
    queryFn: () => adminApi.coaches.get(id!),
    enabled: !!id,
  });
export const useSeats = (coachId: string, x: ListQuery) =>
  useQuery({
    queryKey: ["admin", "seats", coachId, x],
    queryFn: () => adminApi.seats.list(coachId, x),
    enabled: !!coachId,
  });
export const useSeat = (id?: string) =>
  useQuery({
    queryKey: ["admin", "seat", id],
    queryFn: () => adminApi.seats.get(id!),
    enabled: !!id,
  });
export const useJourneys = (x: JourneyListQuery) =>
  useQuery({
    queryKey: [...keys.journeys, x],
    queryFn: () => adminApi.journeys.list(x),
  });
export const useJourney = (id?: string) =>
  useQuery({
    queryKey: [...keys.journeys, id],
    queryFn: () => adminApi.journeys.get(id!),
    enabled: !!id,
  });
export function useAdminMutation<TVars, TData>(
  fn: (v: TVars) => Promise<TData>,
  invalidate: readonly unknown[],
) {
  const c = useQueryClient();
  return useMutation({
    mutationFn: fn,
    onSuccess: async () => {
      await c.invalidateQueries({ queryKey: invalidate });
      await c.invalidateQueries({ queryKey: keys.dashboard });
    },
  });
}
