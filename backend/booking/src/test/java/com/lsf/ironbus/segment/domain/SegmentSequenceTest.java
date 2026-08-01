package com.lsf.ironbus.segment.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SegmentSequenceTest {

    @Test
    void acceptsZeroAndPositiveSequences() {
        assertThat(new SegmentSequence(0).value()).isZero();
        assertThat(new SegmentSequence(4).value()).isEqualTo(4);
    }

    @Test
    void rejectsNegativeSequence() {
        assertThatThrownBy(() -> new SegmentSequence(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Segment sequence cannot be negative");
    }
}
