package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.train.domain.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoachRepository
        extends JpaRepository<Coach, UUID> {

    boolean existsByTrainIdAndCoachNumberIgnoreCase(
            UUID trainId,
            String coachNumber
    );

    Optional<Coach> findByIdAndActiveTrue(UUID id);

    List<Coach> findAllByTrainIdAndActiveTrueOrderByCoachNumberAsc(
            UUID trainId
    );
}