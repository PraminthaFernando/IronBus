package com.lsf.ironbus.segment.domain;

import java.util.List;
import java.util.stream.IntStream;

public record SegmentRange(
        int startInclusive,
        int endExclusive
) {

    public SegmentRange {
        if (startInclusive < 0) {
            throw new IllegalArgumentException(
                    "Segment range start cannot be negative"
            );
        }

        if (endExclusive <= startInclusive) {
            throw new IllegalArgumentException(
                    "Segment range end must be greater than start"
            );
        }
    }

    public boolean overlaps(SegmentRange other) {
        return this.startInclusive < other.endExclusive
                && other.startInclusive < this.endExclusive;
    }

    public boolean isAdjacentTo(SegmentRange other) {
        return this.endExclusive == other.startInclusive
                || other.endExclusive == this.startInclusive;
    }

    public boolean contains(int segmentSequence) {
        return segmentSequence >= startInclusive
                && segmentSequence < endExclusive;
    }

    public int segmentCount() {
        return endExclusive - startInclusive;
    }

    public List<SegmentSequence> segments() {
        return IntStream
                .range(startInclusive, endExclusive)
                .mapToObj(SegmentSequence::new)
                .toList();
    }
}