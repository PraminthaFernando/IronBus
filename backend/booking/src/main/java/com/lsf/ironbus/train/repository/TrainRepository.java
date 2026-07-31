package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.train.domain.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainRepository
        extends JpaRepository<Train, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<Train> findByIdAndActiveTrue(UUID id);

    List<Train> findAllByActiveTrueOrderByNameAsc();

    Optional<Train> findByCodeIgnoreCase(String code);
}