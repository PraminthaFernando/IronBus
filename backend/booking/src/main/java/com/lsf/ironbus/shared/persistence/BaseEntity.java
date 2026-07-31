package com.lsf.ironbus.shared.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected BaseEntity(Instant createdAt) {
        Instant time = Objects.requireNonNull(createdAt, "createdAt is required");

        this.createdAt = time;
        this.updatedAt = time;
    }

    protected void markUpdated(Instant updatedAt) {
        this.updatedAt = Objects.requireNonNull(
                updatedAt,
                "updatedAt is required"
        );
    }
}