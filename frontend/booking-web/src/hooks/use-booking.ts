import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { queryKeys } from "../lib/query-keys";
import { cancelBooking, createBooking, getBooking, searchBookingsByEmail, type CreateBookingRequest } from "../api/bookings-api";

export function useBooking(r?: string) {

  return useQuery({
    queryKey: r ? queryKeys.booking(r) : ["booking", "disabled"],
    queryFn: () => getBooking(r!),
    enabled: !!r,
    retry: false,
  });

}

export function useCreateBooking(j: string, o: string, d: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: (r: CreateBookingRequest) => createBooking(r),
    onSuccess: () =>
      qc.invalidateQueries({ queryKey: queryKeys.availableSeats(j, o, d) }),
  });

}
export function useCancelBooking(r: string) {
  const qc = useQueryClient();

  return useMutation({
    mutationFn: () => cancelBooking(r),
    onSuccess: (b) => qc.setQueryData(queryKeys.booking(r), b),
  });
}

export function useBookingsByEmail(
  email: string | undefined,
  page = 0,
  size = 10,
) {
  return useQuery({
    queryKey: email
      ? queryKeys.bookingsByEmail(
          email,
          page,
          size,
        )
      : ["bookings-by-email", "disabled"],

    queryFn: () =>
      searchBookingsByEmail({
        passengerEmail: email!,
        page,
        size,
      }),

    enabled: Boolean(email),
    retry: false,
  });
}
