package com.lsf.ironbus.booking.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PassengerDetailsTest {
    @Test
    void trimsFields() {
        var p = new PassengerDetails("  A  ", "  a@b.com  ", "  +94  ");
        assertThat(p.name()).isEqualTo("A");
        assertThat(p.email()).isEqualTo("a@b.com");
    }

    @Test
    void rejectsBlank() {
        assertThatThrownBy(
            () -> new PassengerDetails(
                " ",
                "a@b.com",
                "+94"
            )
        )
        .isInstanceOf(IllegalArgumentException.class);
    }
}
