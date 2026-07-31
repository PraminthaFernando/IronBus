package com.lsf.ironbus.train.domain;

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
        name = "ib_trains",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_train_code", columnNames = "code")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Train extends BaseEntity {

    private static final int MAX_CODE_LENGTH = 30;
    private static final int MAX_NAME_LENGTH = 150;

    @Id
    private UUID id;

    @Column(nullable = false, length = MAX_CODE_LENGTH)
    private String code;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(nullable = false)
    private boolean active;

    public Train(
            UUID id,
            String code,
            String name,
            Instant createdAt
    ) {
        super(createdAt);

        this.id = Objects.requireNonNull(id, "Train id is required");
        this.code = normalizeCode(code);
        this.name = validateName(name);
        this.active = true;
    }

    public void rename(String name, Instant updatedAt) {
        this.name = validateName(name);
        markUpdated(updatedAt);
    }

    public void deactivate(Instant updatedAt) {
        this.active = false;
        markUpdated(updatedAt);
    }

    private static String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Train code is required");
        }

        String normalized = code.trim().toUpperCase(Locale.ROOT);

        if (normalized.length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "Train code cannot exceed " + MAX_CODE_LENGTH + " characters"
            );
        }

        return normalized;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Train name is required");
        }

        String normalized = name.trim();

        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Train name cannot exceed " + MAX_NAME_LENGTH + " characters"
            );
        }

        return normalized;
    }
}