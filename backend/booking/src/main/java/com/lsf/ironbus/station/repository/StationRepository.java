package com.lsf.ironbus.station.repository;

import com.lsf.ironbus.station.domain.Station;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StationRepository
        extends JpaRepository<Station, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<Station> findByCodeIgnoreCase(String code);

    List<Station> findAllByActiveTrueOrderByNameAsc();
    
    Page<Station> findAll(@NonNull Pageable pageable);

    Page<Station>
    findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
            String code,
            String name,
            Pageable pageable
    );

    long countByActiveTrue();
    long countByActiveFalse();
}