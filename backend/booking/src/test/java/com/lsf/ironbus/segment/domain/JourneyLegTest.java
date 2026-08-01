package com.lsf.ironbus.segment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JourneyLegTest {

    @Test
    void calculatesDistanceFromCumulativeDistances() {
        JourneyLeg leg = leg(1, 4, "27.00", "207.00", new SegmentRange(1, 4));

        assertThat(leg.distanceKm()).isEqualByComparingTo("180.00");
    }

    @Test
    void supportsSingleSegmentJourney() {
        JourneyLeg leg = leg(2, 3, "120.00", "207.00", new SegmentRange(2, 3));

        assertThat(leg.segmentRange().segmentCount()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidDirectionAndDistance() {
        assertThatThrownBy(() ->
                leg(2, 2, "120.00", "120.00", new SegmentRange(2, 3))
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Origin must appear before destination");

        assertThatThrownBy(() ->
                leg(1, 2, "120.00", "120.00", new SegmentRange(1, 2))
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("Destination distance must be greater");
    }

    private JourneyLeg leg(
            int originSequence,
            int destinationSequence,
            String originDistance,
            String destinationDistance,
            SegmentRange range
    ) {
        return new JourneyLeg(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                originSequence,
                destinationSequence,
                new BigDecimal(originDistance),
                new BigDecimal(destinationDistance),
                range
        );
    }
}
