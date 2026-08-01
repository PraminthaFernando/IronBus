package com.lsf.ironbus.segment;

import com.lsf.ironbus.journey.domain.Journey;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.domain.Route;
import com.lsf.ironbus.route.domain.RouteStation;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.route.repository.RouteStationRepository;
import com.lsf.ironbus.segment.app.service.JourneyLegQuoteService;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.station.domain.Station;
import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.support.PostgreSqlIntegrationTest;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.enums.TravelClass;
import com.lsf.ironbus.train.repository.TrainRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase2Fixtures.NOW;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate",
        "app.fare.currency=LKR",
        "app.fare.base-fare=100.00",
        "app.fare.price-per-km=8.00",
        "app.fare.minimum-fare=150.00",
        "app.fare.class-multipliers.FIRST_CLASS=1.75",
        "app.fare.class-multipliers.SECOND_CLASS=1.25",
        "app.fare.class-multipliers.THIRD_CLASS=1.00"
})
@Transactional
class JourneyLegQuoteIntegrationTest extends PostgreSqlIntegrationTest {

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    RouteStationRepository routeStationRepository;

    @Autowired
    JourneyRepository journeyRepository;

    @Autowired
    JourneyLegQuoteService quoteService;

    @Test
    void resolvesLegAndCalculatesFareAgainstPostgreSql() {
        Train train = trainRepository.save(new Train(
                UUID.randomUUID(),
                "ITT-" + shortId(),
                "Integration Train",
                NOW
        ));
        Route route = routeRepository.save(new Route(
                UUID.randomUUID(),
                "ITR-" + shortId(),
                "Integration Route",
                NOW
        ));
        Station colombo = stationRepository.save(new Station(
                UUID.randomUUID(),
                "C" + shortId().substring(0, 5),
                "Colombo Fort",
                NOW
        ));
        Station gampaha = stationRepository.save(new Station(
                UUID.randomUUID(),
                "G" + shortId().substring(0, 5),
                "Gampaha",
                NOW
        ));
        Station kandy = stationRepository.save(new Station(
                UUID.randomUUID(),
                "K" + shortId().substring(0, 5),
                "Kandy",
                NOW
        ));

        routeStationRepository.save(new RouteStation(
                UUID.randomUUID(),
                route,
                colombo,
                0,
                BigDecimal.ZERO,
                0,
                NOW
        ));
        routeStationRepository.save(new RouteStation(
                UUID.randomUUID(),
                route,
                gampaha,
                1,
                new BigDecimal("27.00"),
                35,
                NOW
        ));
        routeStationRepository.save(new RouteStation(
                UUID.randomUUID(),
                route,
                kandy,
                2,
                new BigDecimal("120.00"),
                175,
                NOW
        ));

        Journey journey = journeyRepository.saveAndFlush(new Journey(
                UUID.randomUUID(),
                train,
                route,
                NOW.plusSeconds(86_400),
                NOW
        ));

        var response = quoteService.quote(
                new ResolveJourneyLegCommand(
                        journey.getId(),
                        colombo.getId(),
                        kandy.getId()
                ),
                TravelClass.SECOND_CLASS
        );

        assertThat(response.segmentSequences()).containsExactly(0, 1);
        assertThat(response.distanceKm()).isEqualByComparingTo("120.00");
        assertThat(response.fareAmount()).isEqualByComparingTo("1300.00");
        assertThat(response.currency()).isEqualTo("LKR");
    }

    private static String shortId() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
