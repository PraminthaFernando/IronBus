package com.lsf.ironbus.booking.app.response;

import java.util.List;

public record BookingSearchResponse(
        List<BookingSearchItemResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public BookingSearchResponse {
        items = List.copyOf(items);
    }
}