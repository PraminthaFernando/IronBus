import {
  Link,
  useNavigate,
  useParams,
} from "react-router-dom";

import { useBooking } from "../hooks/use-booking";

import { PageContainer } from "../components/common/PageContainer";
import { BookingConfirmation } from "../components/booking/BookingConfirmation";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { ErrorMessage } from "../components/common/ErrorMessage";

export function BookingConfirmationPage() {
  const navigate = useNavigate();
  const { reference } = useParams();

  const bookingQuery = useBooking(reference);

  if (!reference) {
    return (
      <PageContainer>
        <section className="confirmation-error-state">
          <ErrorMessage message="The booking reference is missing." />

          <button
            type="button"
            className="button button--primary"
            onClick={() =>
              navigate("/journeys/search")
            }
          >
            Return to journey search
          </button>
        </section>
      </PageContainer>
    );
  }

  if (bookingQuery.isLoading) {
    return (
      <PageContainer>
        <LoadingSpinner label="Loading your booking confirmation..." />
      </PageContainer>
    );
  }

  if (bookingQuery.isError) {
    return (
      <PageContainer>
        <section className="confirmation-error-state">
          <ErrorMessage message="The booking confirmation could not be loaded. Please try again." />

          <div className="confirmation-error-state__actions">
            <button
              type="button"
              className="button button--secondary"
              onClick={() =>
                navigate("/journeys/search")
              }
            >
              Back to search
            </button>

            <button
              type="button"
              className="button button--primary"
              onClick={() =>
                bookingQuery.refetch()
              }
            >
              Retry
            </button>
          </div>
        </section>
      </PageContainer>
    );
  }

  if (!bookingQuery.data) {
    return null;
  }

  return (
    <PageContainer>
      <BookingConfirmation
        booking={bookingQuery.data}
      />

      <nav
        className="confirmation-page-actions"
        aria-label="Booking actions"
      >
        <Link
          to="/journeys/search"
          className="button button--secondary"
        >
          Book another journey
        </Link>

        <Link
          to={`/manage-booking?reference=${encodeURIComponent(
            bookingQuery.data.reference,
          )}`}
          className="button button--primary"
        >
          Manage booking
        </Link>
      </nav>
    </PageContainer>
  );
}