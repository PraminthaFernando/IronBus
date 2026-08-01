package com.lsf.ironbus.train.domain;

import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.lsf.ironbus.support.Phase1BFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoachTest {

    @Test
    void createsReservedCoach() {
        Coach coach = new Coach(UUID.randomUUID(), train(), " r1 ",
                TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED, NOW);
        assertThat(coach.getCoachNumber()).isEqualTo("R1");
        assertThat(coach.isReserved()).isTrue();
        assertThat(coach.isActive()).isTrue();
    }

    @Test
    void identifiesUnreservedCoach() {
        Coach coach = new Coach(UUID.randomUUID(), train(), "U1",
                TravelClass.THIRD_CLASS, CoachReservationMode.UNRESERVED, NOW);
        assertThat(coach.isReserved()).isFalse();
    }

    @Test
    void rejectsMissingTravelClass() {
        Train train = train("COACH-TEST-1");

        assertThatThrownBy(() -> new Coach(
                UUID.randomUUID(),
                train,
                "R1",
                null,
                CoachReservationMode.RESERVED,
                NOW
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Travel class is required");
    }
}
