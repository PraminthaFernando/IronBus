package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.support.PostgreSqlIntegrationTest;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Seat;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.enums.CoachReservationMode;
import com.lsf.ironbus.train.enums.SeatType;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static com.lsf.ironbus.support.Phase1BFixtures.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
    "spring.flyway.enabled=true",
    "spring.jpa.hibernate.ddl-auto=validate"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeatRepositoryTest extends PostgreSqlIntegrationTest {
    @Autowired
    TrainRepository trainRepository;
    @Autowired
    CoachRepository coachRepository;
    @Autowired
    SeatRepository seatRepository;

    @Test
    void preventsDuplicateSeatNumberWithinSameCoach() {
        Train train = trainRepository.save(new Train(UUID.randomUUID(), "SEAT-TRAIN-1", "Seat Test Train", NOW));
        Coach coach = coachRepository.save(new Coach(UUID.randomUUID(), train, "R1", TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED, NOW));

        seatRepository.saveAndFlush(new Seat(
                UUID.randomUUID(),
                coach,
                "1A",
                SeatType.WINDOW,
                1,
                1,
                NOW,
                true
        ));

        assertThatThrownBy(() -> seatRepository.saveAndFlush(
                new Seat(
                        UUID.randomUUID(),
                        coach,
                        "1A",
                        SeatType.AISLE,
                        1,
                        2,
                        NOW,
                        true
                )
        ))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void allowsSameSeatNumberInDifferentCoaches() {
        Train train = trainRepository.save(new Train(UUID.randomUUID(), "SEAT-TRAIN-2", "Seat Test Train", NOW));
        Coach first = coachRepository.save(new Coach(UUID.randomUUID(), train, "R1", TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED, NOW));
        Coach second = coachRepository.save(new Coach(UUID.randomUUID(), train, "R2", TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED, NOW));
        seatRepository.save(new Seat(
                UUID.randomUUID(),
                first,
                "1A",
                SeatType.WINDOW,
                1,
                1,
                NOW,
                true
        ));
        seatRepository.save(new Seat(
                UUID.randomUUID(),
                second,
                "1A",
                SeatType.WINDOW,
                1,
                1,
                NOW,
                true
        ));
        seatRepository.flush();
        assertThat(seatRepository.count()).isGreaterThanOrEqualTo(2);
    }
}
