package com.lsf.ironbus.segment.domain;

public record SegmentSequence(int value) {

    public SegmentSequence {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "Segment sequence cannot be negative"
            );
        }
    }
}