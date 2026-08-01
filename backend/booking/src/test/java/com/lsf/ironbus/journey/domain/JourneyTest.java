package com.lsf.ironbus.journey.domain;

import com.lsf.ironbus.journey.enums.JourneyStatus;
import org.junit.jupiter.api.Test;

import static com.lsf.ironbus.support.Phase1BFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JourneyTest {

    @Test
    void newJourneyStartsScheduledAndBookable() {
        Journey journey = journey(train(), route());
        assertThat(journey.getStatus()).isEqualTo(JourneyStatus.SCHEDULED);
        assertThat(journey.isBookable()).isTrue();
    }

    @Test
    void followsValidLifecycle() {
        Journey journey = journey(train(), route());
        journey.markBoarding(NOW.plusSeconds(10));
        journey.markDeparted(NOW.plusSeconds(20));
        journey.markCompleted(NOW.plusSeconds(30));
        assertThat(journey.getStatus()).isEqualTo(JourneyStatus.COMPLETED);
        assertThat(journey.isBookable()).isFalse();
    }

    @Test
    void rejectsInvalidTransition() {
        Journey journey = journey(train(), route());
        assertThatThrownBy(() -> journey.markDeparted(NOW.plusSeconds(10)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Journey must be BOARDING");
    }

    @Test
    void completedJourneyCannotBeCancelled() {
        Journey journey = journey(train(), route());
        journey.markBoarding(NOW.plusSeconds(10));
        journey.markDeparted(NOW.plusSeconds(20));
        journey.markCompleted(NOW.plusSeconds(30));
        assertThatThrownBy(() -> journey.cancel(NOW.plusSeconds(40)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("A completed journey cannot be cancelled");
    }
}
