import { Navigate, Route, Routes } from 'react-router-dom';
import { JourneySearchPage } from '../pages/JourneySearchPage';
import { SeatSelectionPage } from '../pages/SeatSelectionPage';
import { BookingDetailsPage } from '../pages/BookingDetailsPage';
import { BookingConfirmationPage } from '../pages/BookingConfirmationPage';
import { ManageBookingPage } from '../pages/ManageBookingPage';

export function AppRouter() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/journeys/search" replace />} />
            <Route path="/journeys/search" element={<JourneySearchPage />} />
            <Route path="/journeys/:journeyId/seats" element={<SeatSelectionPage />} />
            <Route path="/journeys/:journeyId/book" element={<BookingDetailsPage />} />
            <Route path="/bookings/:reference/confirmation" element={<BookingConfirmationPage />} />
            <Route path="/manage-booking" element={<ManageBookingPage />} />
        </Routes>
    );
}