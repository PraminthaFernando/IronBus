package com.lsf.ironbus.shared.config;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.shared.domain.TimeProvider;
import com.lsf.ironbus.shared.domain.UuidGenerator;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevelopmentJourneyDataInitializer
        implements CommandLineRunner {

    private static final ZoneId COLOMBO =
            ZoneId.of("Asia/Colombo");

    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final JourneyRepository journeyRepository;
    private final UuidGenerator uuidGenerator;
    private final TimeProvider timeProvider;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        Train train = trainRepository
                .findByCodeIgnoreCase("UDR-001")
                .orElseThrow();

        Route route = routeRepository
                .findByCodeIgnoreCase("FOT-BAD")
                .orElseThrow();

        LocalDate tomorrow = LocalDate.now(COLOMBO).plusDays(1);

        var departure = tomorrow
                .atTime(LocalTime.of(5, 30))
                .atZone(COLOMBO)
                .toInstant();

        if (!journeyRepository.existsByTrainIdAndDepartureTime(
                train.getId(),
                departure
        )) {
            journeyRepository.save(new Journey(
                    uuidGenerator.generate(),
                    train,
                    route,
                    departure,
                    timeProvider.now()
            ));
        }
    }
}