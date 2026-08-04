import type { RouteObject } from "react-router-dom";
import { AdminLayout } from "../layouts/AdminLayout";
import { AdminDashboardPage } from "../pages/AdminDashboardPage";
import { StationListPage } from "../pages/stations/StationListPage";
import { StationFormPage } from "../pages/stations/StationFormPage";
import { RouteListPage } from "../pages/routes/RouteListPage";
import { RouteFormPage } from "../pages/routes/RouteFormPage";
import { RouteStationsPage } from "../pages/routes/RouteStationsPage";
import { TrainListPage } from "../pages/trains/TrainListPage";
import { TrainFormPage } from "../pages/trains/TrainFormPage";
import { CoachListPage } from "../pages/coaches/CoachListPage";
import { CoachFormPage } from "../pages/coaches/CoachFormPage";
import { SeatListPage } from "../pages/seats/SeatListPage";
import { SeatFormPage } from "../pages/seats/SeatFormPage";
import { SeatBulkCreatePage } from "../pages/seats/SeatBulkCreatePage";
import { JourneyListPage } from "../pages/journeys/JourneyListPage";
import { JourneyFormPage } from "../pages/journeys/JourneyFormPage";
export const adminRoute: RouteObject = {
  path: "/admin",
  element: <AdminLayout />,
  children: [
    { index: true, element: <AdminDashboardPage /> },
    { path: "stations", element: <StationListPage /> },
    { path: "stations/new", element: <StationFormPage /> },
    { path: "stations/:stationId/edit", element: <StationFormPage /> },
    { path: "routes", element: <RouteListPage /> },
    { path: "routes/new", element: <RouteFormPage /> },
    { path: "routes/:routeId/edit", element: <RouteFormPage /> },
    { path: "routes/:routeId/stations", element: <RouteStationsPage /> },
    { path: "trains", element: <TrainListPage /> },
    { path: "trains/new", element: <TrainFormPage /> },
    { path: "trains/:trainId/edit", element: <TrainFormPage /> },
    { path: "trains/:trainId/coaches", element: <CoachListPage /> },
    { path: "trains/:trainId/coaches/new", element: <CoachFormPage /> },
    { path: "coaches/:coachId/edit", element: <CoachFormPage /> },
    { path: "coaches/:coachId/seats", element: <SeatListPage /> },
    { path: "coaches/:coachId/seats/new", element: <SeatFormPage /> },
    { path: "coaches/:coachId/seats/bulk", element: <SeatBulkCreatePage /> },
    { path: "seats/:seatId/edit", element: <SeatFormPage /> },
    { path: "journeys", element: <JourneyListPage /> },
    { path: "journeys/new", element: <JourneyFormPage /> },
    { path: "journeys/:journeyId/edit", element: <JourneyFormPage /> },
  ],
};
