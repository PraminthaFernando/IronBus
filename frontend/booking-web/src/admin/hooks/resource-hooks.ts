import { adminApi } from "../api/admin-api";
import {
  useAdminMutation,
  useCoaches,
  useCoach,
  useJourney,
  useJourneys,
  useRoute,
  useRoutes,
  useRouteStations,
  useSeat,
  useSeats,
  useStation,
  useStations,
  useTrain,
  useTrains,
} from "./use-admin";

export {
  useCoaches,
  useCoach,
  useJourney,
  useJourneys,
  useRoute,
  useRoutes,
  useRouteStations,
  useSeat,
  useSeats,
  useStation,
  useStations,
  useTrain,
  useTrains,
};

export const mutations = {
  stationCreate: () =>
    useAdminMutation(adminApi.stations.create, ["admin", "stations"]),
  stationUpdate: (id: string) =>
    useAdminMutation(
      (b: any) => adminApi.stations.update(id, b),
      ["admin", "stations"],
    ),
  routeCreate: () =>
    useAdminMutation(adminApi.routes.create, ["admin", "routes"]),
  routeUpdate: (id: string) =>
    useAdminMutation(
      (b: any) => adminApi.routes.update(id, b),
      ["admin", "routes"],
    ),
  routeStations: (id: string) =>
    useAdminMutation(
      (b: any) => adminApi.routes.saveStations(id, b),
      ["admin", "routes"],
    ),
  trainCreate: () =>
    useAdminMutation(adminApi.trains.create, ["admin", "trains"]),
  trainUpdate: (id: string) =>
    useAdminMutation(
      (b: any) => adminApi.trains.update(id, b),
      ["admin", "trains"],
    ),
  coachCreate: (trainId: string) =>
    useAdminMutation(
      (b: any) => adminApi.coaches.create(trainId, b),
      ["admin", "coaches"],
    ),
  coachUpdate: (id: string) =>
    useAdminMutation(
      (b: any) => adminApi.coaches.update(id, b),
      ["admin", "coaches"],
    ),
  seatCreate: (coachId: string) =>
    useAdminMutation(
      (b: any) => adminApi.seats.create(coachId, b),
      ["admin", "seats"],
    ),
  seatUpdate: (id: string) =>
    useAdminMutation(
      (b: any) => adminApi.seats.update(id, b),
      ["admin", "seats"],
    ),
  seatBulk: (coachId: string) =>
    useAdminMutation(
      (b: any) => adminApi.seats.bulk(coachId, b),
      ["admin", "seats"],
    ),
  journeyCreate: () =>
    useAdminMutation(adminApi.journeys.create, ["admin", "journeys"]),
  journeyUpdate: (id: string) =>
    useAdminMutation(
      (b: any) => adminApi.journeys.update(id, b),
      ["admin", "journeys"],
    ),
};
