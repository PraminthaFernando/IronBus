package com.lsf.ironbus.station.repository;

import com.lsf.ironbus.station.domain.Station;
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
}