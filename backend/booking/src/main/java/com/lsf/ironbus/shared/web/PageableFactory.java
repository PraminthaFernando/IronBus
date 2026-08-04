package com.lsf.ironbus.shared.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableFactory {

    private static final int MAX_PAGE_SIZE = 100;

    private PageableFactory() {
    }

    public static Pageable create(
            int page,
            int size,
            String sort,
            Set<String> allowedSortProperties,
            String defaultSort
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, MAX_PAGE_SIZE);

        Sort.Order order = parseSort(
                sort,
                allowedSortProperties,
                defaultSort
        );

        return PageRequest.of(
                safePage,
                safeSize,
                Sort.by(order)
        );
    }

    private static Sort.Order parseSort(
            String sort,
            Set<String> allowed,
            String defaultProperty
    ) {
        if (sort == null || sort.isBlank()) {
            return Sort.Order.asc(defaultProperty);
        }

        String[] parts = sort.split(",", 2);
        String property = parts[0];

        if (!allowed.contains(property)) {
            property = defaultProperty;
        }

        Sort.Direction direction =
                parts.length > 1
                        && "desc".equalsIgnoreCase(parts[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return new Sort.Order(direction, property);
    }
}