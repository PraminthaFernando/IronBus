package com.lsf.ironbus.admin.app;

import com.lsf.ironbus.admin.repository.AdminWarningQueryRepository;
import com.lsf.ironbus.admin.web.response.AdminDashboardResponse;
import com.lsf.ironbus.journey.app.response.JourneyAdminMapper;
import com.lsf.ironbus.journey.enums.JourneyStatus;
import com.lsf.ironbus.journey.repository.JourneyRepository;
import com.lsf.ironbus.route.repository.RouteRepository;
import com.lsf.ironbus.station.repository.StationRepository;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.repository.CoachRepository;
import com.lsf.ironbus.train.repository.SeatRepository;
import com.lsf.ironbus.train.repository.TrainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final JourneyRepository journeyRepository;
    private final AdminWarningQueryRepository warningRepository;
    private final JourneyAdminMapper journeyMapper;
    private final Clock clock;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        Instant now = clock.instant();

        ZoneId zone = ZoneId.of("Asia/Colombo");
        LocalDate today = LocalDate.now(clock.withZone(zone));

        Instant startOfToday =
                today.atStartOfDay(zone).toInstant();

        Instant startOfTomorrow =
                today.plusDays(1)
                        .atStartOfDay(zone)
                        .toInstant();

        var upcoming = journeyRepository
                .findTop10ByStatusAndDepartureTimeAfterOrderByDepartureTimeAsc(
                        JourneyStatus.SCHEDULED,
                        now
                );

        return new AdminDashboardResponse(
                new AdminDashboardResponse.Metric(
                        stationRepository.count(),
                        stationRepository.countByActiveTrue()
                ),
                new AdminDashboardResponse.Metric(
                        routeRepository.count(),
                        routeRepository.countByActiveTrue()
                ),
                new AdminDashboardResponse.Metric(
                        trainRepository.count(),
                        trainRepository.countByActiveTrue()
                ),
                new AdminDashboardResponse.CoachMetric(
                        coachRepository.count(),
                        coachRepository.countByActiveTrue(),
                        coachRepository.countByReservationMode(
                                CoachReservationMode.RESERVED
                        ),
                        coachRepository.countByReservationMode(
                                CoachReservationMode.UNRESERVED
                        )
                ),
                new AdminDashboardResponse.Metric(
                        seatRepository.count(),
                        seatRepository.countByActiveTrue()
                ),
                new AdminDashboardResponse.JourneyMetric(
                        journeyRepository.count(),
                        journeyRepository.countByStatusNot(
                                JourneyStatus.CANCELLED
                        ),
                        journeyRepository
                                .countByDepartureTimeAfterAndStatus(
                                        now,
                                        JourneyStatus.SCHEDULED
                                ),
                        journeyRepository
                                .countByDepartureTimeGreaterThanEqualAndDepartureTimeLessThan(
                                        startOfToday,
                                        startOfTomorrow
                                ),
                        journeyRepository.countByStatus(
                                JourneyStatus.CANCELLED
                        )
                ),
                warningRepository.findWarnings(),
                upcoming.stream()
                        .map(journeyMapper::toAdminResponse)
                        .toList()
        );
    }
}