import type { BookingResponse } from "../types/domain";
import { apiClient } from "./api-client";

export interface CreateBookingRequest {
  journeyId: string;
  seatId: string;
  originStationId: string;
  destinationStationId: string;
  passenger: { name: string; email: string; phone: string };
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
