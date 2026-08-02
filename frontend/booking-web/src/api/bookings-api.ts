import type { BookingResponse, BookingSearchResponse } from "../types/domain";
import { apiClient } from "./api-client";

export interface CreateBookingRequest {
  journeyId: string;
  seatId: string;
  originStationId: string;
  destinationStationId: string;
  passenger: { name: string; email: string; phone: string };
}

export interface SearchBookingsRequest {
  passengerEmail: string;
  page?: number;
  size?: number;
}

export async function createBooking(
  r: CreateBookingRequest,
): Promise<BookingResponse> {
  return (await apiClient.post<BookingResponse>("/bookings", r)).data;
}

export async function getBooking(ref: string): Promise<BookingResponse> {
  return (
    await apiClient.get<BookingResponse>(`/bookings/${encodeURIComponent(ref)}`)
  ).data;
}

export async function cancelBooking(ref: string): Promise<BookingResponse> {
  return (
    await apiClient.post<BookingResponse>(
      `/bookings/${encodeURIComponent(ref)}/cancel`,
    )
  ).data;
}

export async function searchBookingsByEmail({
  passengerEmail,
  page = 0,
  size = 10,
}: SearchBookingsRequest): Promise<BookingSearchResponse> {
  const response =
    await apiClient.post<BookingSearchResponse>(
      "/bookings/search",
      {
        passengerEmail,
      },
      {
        params: {
          page,
          size,
        },
      },
    );

  return response.data;
}
