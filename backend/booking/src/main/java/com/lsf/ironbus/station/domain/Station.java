package com.lsf.ironbus.station.domain;

import com.lsf.ironbus.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Id
    private UUID id;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private boolean active;

    public Station(UUID id, String code, String name) {
        this.id = Objects.requireNonNull(id);
        this.code = normalizeCode(code);
        this.name = validateName(name);
        this.active = true;
    }

    private static String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Station code is required");
        }

        return code.trim().toUpperCase(Locale.ROOT);
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Station name is required");
        }

        return name.trim();
    }
}