package com.lsf.ironbus.journey.repository;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.support.PostgreSqlIntegrationTest;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.repository.TrainRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase1BFixtures.NOW;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JourneyRepositoryTest extends PostgreSqlIntegrationTest {
    @Autowired JourneyRepository journeyRepository;
    @Autowired TrainRepository trainRepository;
    @Autowired RouteRepository routeRepository;

    @Test
    void preventsSameTrainFromHavingDuplicateDepartureTime() {
        Train train = trainRepository.save(new Train(UUID.randomUUID(), "JOURNEY-TRAIN-1", "Journey Test Train", NOW));
        Route route = routeRepository.save(new Route(UUID.randomUUID(), "JOURNEY-ROUTE-1", "Journey Test Route", NOW));
        Instant departure = NOW.plusSeconds(86_400);
        journeyRepository.saveAndFlush(new Journey(UUID.randomUUID(), train, route, departure, NOW));
        assertThatThrownBy(() -> journeyRepository.saveAndFlush(
                new Journey(UUID.randomUUID(), train, route, departure, NOW)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findsScheduledJourneysWithinRange() {
        Train train = trainRepository.save(new Train(UUID.randomUUID(), "JOURNEY-TRAIN-2", "Journey Test Train", NOW));
        Route route = routeRepository.save(new Route(UUID.randomUUID(), "JOURNEY-ROUTE-2", "Journey Test Route", NOW));
        Instant departure = NOW.plusSeconds(86_400);
        Journey journey = journeyRepository.saveAndFlush(new Journey(UUID.randomUUID(), train, route, departure, NOW));
        var results = journeyRepository.findAllByRouteIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndStatus(
                route.getId(), departure.minusSeconds(60), departure.plusSeconds(60), JourneyStatus.SCHEDULED);
        assertThat(results).extracting(Journey::getId).contains(journey.getId());
    }
}
