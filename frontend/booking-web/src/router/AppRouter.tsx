import { Navigate, Route, Routes } from 'react-router-dom';
import { JourneySearchPage } from '../pages/JourneySearchPage';
import { SeatSelectionPage } from '../pages/SeatSelectionPage';
import { BookingDetailsPage } from '../pages/BookingDetailsPage';
import { BookingConfirmationPage } from '../pages/BookingConfirmationPage';
import { ManageBookingPage } from '../pages/ManageBookingPage';
import { AdminLayout } from '../admin/layouts/AdminLayout';
import { JourneyFormPage } from '../admin/pages/journeys/JourneyFormPage';
import { JourneyListPage } from '../admin/pages/journeys/JourneyListPage';
import { SeatFormPage } from '../admin/pages/seats/SeatFormPage';
import { SeatBulkCreatePage } from '../admin/pages/seats/SeatBulkCreatePage';
import { SeatListPage } from '../admin/pages/seats/SeatListPage';
import { CoachFormPage } from '../admin/pages/coaches/CoachFormPage';
import { CoachListPage } from '../admin/pages/coaches/CoachListPage';
import { TrainFormPage } from '../admin/pages/trains/TrainFormPage';
import { TrainListPage } from '../admin/pages/trains/TrainListPage';
import { RouteStationsPage } from '../admin/pages/routes/RouteStationsPage';
import { RouteFormPage } from '../admin/pages/routes/RouteFormPage';
import { RouteListPage } from '../admin/pages/routes/RouteListPage';
import { AdminDashboardPage } from '../admin/pages/AdminDashboardPage';
import { StationListPage } from '../admin/pages/stations/StationListPage';
import { StationFormPage } from '../admin/pages/stations/StationFormPage';

export function AppRouter() {
    return (
        <><Routes>
            <Route path="/" element={<Navigate to="/journeys/search" replace />} />
            <Route path="/journeys/search" element={<JourneySearchPage />} />
            <Route path="/journeys/:journeyId/seats" element={<SeatSelectionPage />} />
            <Route path="/journeys/:journeyId/book" element={<BookingDetailsPage />} />
            <Route path="/bookings/:reference/confirmation" element={<BookingConfirmationPage />} />
            <Route path="/manage-booking" element={<ManageBookingPage />} />
            <Route
                path="/admin"
                element={<AdminLayout />}
            >
                <Route
                    index
                    element={<AdminDashboardPage />} />

                <Route
                    path="stations"
                    element={<StationListPage />} />

                <Route
                    path="stations/new"
                    element={<StationFormPage />} />

                <Route
                    path="stations/:stationId/edit"
                    element={<StationFormPage />} />

                <Route
                    path="routes"
                    element={<RouteListPage />} />

                <Route
                    path="routes/new"
                    element={<RouteFormPage />} />

                <Route
                    path="routes/:routeId/edit"
                    element={<RouteFormPage />} />

                <Route
                    path="routes/:routeId/stations"
                    element={<RouteStationsPage />} />

                <Route
                    path="trains"
                    element={<TrainListPage />} />

                <Route
                    path="trains/new"
                    element={<TrainFormPage />} />

                <Route
                    path="trains/:trainId/edit"
                    element={<TrainFormPage />} />

                <Route
                    path="trains/:trainId/coaches"
                    element={<CoachListPage />} />

                <Route
                    path="trains/:trainId/coaches/new"
                    element={<CoachFormPage />} />

                <Route
                    path="coaches/:coachId/edit"
                    element={<CoachFormPage />} />

                <Route
                    path="coaches/:coachId/seats"
                    element={<SeatListPage />} />

                <Route
                    path="coaches/:coachId/seats/new"
                    element={<SeatFormPage />} />

                <Route
                    path="coaches/:coachId/seats/bulk"
                    element={<SeatBulkCreatePage />} />

                <Route
                    path="seats/:seatId/edit"
                    element={<SeatFormPage />} />

                <Route
                    path="journeys"
                    element={<JourneyListPage />} />

                <Route
                    path="journeys/new"
                    element={<JourneyFormPage />} />

                <Route
                    path="journeys/:journeyId/edit"
                    element={<JourneyFormPage />} />
            </Route>
        </Routes></>
    );
}