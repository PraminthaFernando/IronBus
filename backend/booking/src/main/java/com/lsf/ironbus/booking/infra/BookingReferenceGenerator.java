package com.lsf.ironbus.booking.infra;

import com.lsf.ironbus.booking.domain.BookingReference;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class BookingReferenceGenerator {

    private static final char[] ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final SecureRandom random = new SecureRandom();

    public BookingReference generate() {
        int year = ZonedDateTime
                .now(ZoneOffset.UTC)
                .getYear() % 100;

        StringBuilder suffix = new StringBuilder(6);

        for (int index = 0; index < 6; index++) {
            suffix.append(
                    ALPHABET[random.nextInt(ALPHABET.length)]
            );
        }

        return new BookingReference(
                "LSF-%02d-%s".formatted(year, suffix)
        );
    }
}