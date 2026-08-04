package com.lsf.ironbus.station.domain;

import com.lsf.ironbus.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "ib_stations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_station_code", columnNames = "code")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Station extends BaseEntity {

    private static final int MAX_CODE_LENGTH = 20;
    private static final int MAX_NAME_LENGTH = 150;

    @Id
    private UUID id;

    @Column(nullable = false, length = MAX_CODE_LENGTH)
    private String code;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(nullable = false)
    private boolean active;

    public Station(
            UUID id,
            String code,
            String name,
            Instant createdAt
    ) {
        super(createdAt);

        this.id = Objects.requireNonNull(id, "Station id is required");
        this.code = normalizeCode(code);
        this.name = validateName(name);
        this.active = true;
    }

    public void update(
            String code,
            String name,
            boolean active
    ) {
        this.code = normalizeCode(code);
        this.name = validateName(name);
        this.active = active;
    }

    public void rename(String name, Instant updatedAt) {
        this.name = validateName(name);
        markUpdated(updatedAt);
    }

    public void deactivate(Instant updatedAt) {
        this.active = false;
        markUpdated(updatedAt);
    }

    public void activate(Instant updatedAt) {
        this.active = true;
        markUpdated(updatedAt);
    }

    private static String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Station code is required");
        }

        String normalized = code.trim().toUpperCase(Locale.ROOT);

        if (normalized.length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Station code cannot exceed " + MAX_CODE_LENGTH + " characters"
            );
        }

        return normalized;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Station name is required");
        }

        String normalized = name.trim();

        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Station name cannot exceed " + MAX_NAME_LENGTH + " characters"
            );
        }

        return normalized;
    }
}