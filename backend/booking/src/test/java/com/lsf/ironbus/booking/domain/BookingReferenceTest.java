package com.lsf.ironbus.booking.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingReferenceTest {
    @Test
    void normalizes() {
        assertThat(
            new BookingReference(
                " lsf-26-ab12cd "
            )
                .value()
        )
            .isEqualTo("LSF-26-AB12CD");
    }

    @Test
    void rejectsInvalid() {
        assertThatThrownBy(
            () -> new BookingReference("BAD")
        )
        .isInstanceOf(IllegalArgumentException.class);
    }
}
