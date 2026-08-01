package com.lsf.ironbus.segment.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SegmentRangeTest {

    @Test
    void detectsPartialAndContainedOverlap() {
        assertThat(new SegmentRange(0, 2).overlaps(new SegmentRange(1, 4))).isTrue();
        assertThat(new SegmentRange(0, 5).overlaps(new SegmentRange(2, 3))).isTrue();
    }

    @Test
    void sameRangesOverlap() {
        assertThat(new SegmentRange(0, 2).overlaps(new SegmentRange(0, 2))).isTrue();
    }

    @Test
    void adjacentRangesDoNotOverlap() {
        SegmentRange first = new SegmentRange(0, 2);
        SegmentRange second = new SegmentRange(2, 5);

        assertThat(first.overlaps(second)).isFalse();
        assertThat(first.isAdjacentTo(second)).isTrue();
    }

    @Test
    void separatedRangesDoNotOverlap() {
        SegmentRange first = new SegmentRange(0, 1);
        SegmentRange second = new SegmentRange(3, 5);

        assertThat(first.overlaps(second)).isFalse();
        assertThat(first.isAdjacentTo(second)).isFalse();
    }

    @Test
    void reportsContainsAndSegmentCount() {
        SegmentRange range = new SegmentRange(1, 4);

        assertThat(range.contains(0)).isFalse();
        assertThat(range.contains(1)).isTrue();
        assertThat(range.contains(3)).isTrue();
        assertThat(range.contains(4)).isFalse();
        assertThat(range.segmentCount()).isEqualTo(3);
    }

    @Test
    void generatesSegmentsInAscendingOrder() {
        assertThat(new SegmentRange(1, 4).segments())
                .extracting(SegmentSequence::value)
                .containsExactly(1, 2, 3);
    }

    @Test
    void rejectsInvalidRanges() {
        assertThatThrownBy(() -> new SegmentRange(-1, 2))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new SegmentRange(2, 2))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new SegmentRange(3, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
