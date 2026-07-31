package com.lsf.ironbus.train.domain;

import com.lsf.ironbus.shared.persistence.BaseEntity;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.TravelClass;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "ib_coaches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_train_coach_number",
                        columnNames = {"train_id", "coach_number"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coach extends BaseEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "coach_number", nullable = false, length = 20)
    private String coachNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_class", nullable = false, length = 30)
    private TravelClass travelClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_mode", nullable = false, length = 20)
    private CoachReservationMode reservationMode;

    @Column(nullable = false)
    private boolean active;
}