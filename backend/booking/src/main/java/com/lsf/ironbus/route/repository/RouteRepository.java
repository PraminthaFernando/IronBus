package com.lsf.ironbus.route.repository;

import com.lsf.ironbus.route.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository
        extends JpaRepository<Route, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<Route> findByIdAndActiveTrue(UUID id);

    List<Route> findAllByActiveTrueOrderByNameAsc();

    Optional<Route> findByCodeIgnoreCase(String code);

    long countByActiveTrue();
}