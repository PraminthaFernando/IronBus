package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.support.PostgreSqlIntegrationTest;
import com.lsf.ironbus.train.domain.Coach;
import com.lsf.ironbus.train.domain.Train;
import com.lsf.ironbus.train.enums.CoachReservationMode;
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

@DataJpaTest(properties =
        {
                "spring.flyway.enabled=true",
                "spring.jpa.hibernate.ddl-auto=validate"
        })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CoachRepositoryTest extends PostgreSqlIntegrationTest {
    @Autowired TrainRepository trainRepository;
    @Autowired CoachRepository coachRepository;

    @Test
    void preventsDuplicateCoachNumberWithinSameTrain() {
        Train train = trainRepository.saveAndFlush(new Train(UUID.randomUUID(), "COACH-TRAIN-1", "Coach Test Train", NOW));
        coachRepository.saveAndFlush(new Coach(UUID.randomUUID(), train, "R1", TravelClass.SECOND_CLASS, CoachReservationMode.RESERVED, NOW));
        assertThatThrownBy(() -> coachRepository.saveAndFlush(
                new Coach(UUID.randomUUID(), train, "R1", TravelClass.FIRST_CLASS, CoachReservationMode.RESERVED, NOW)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void allowsSameCoachNumberOnDifferentTrains() {
        Train firstTrain = trainRepository.save(new Train(
                UUID.randomUUID(),
                "COACH-TRAIN-2",
                "First Train",
                NOW
        ));

        Train secondTrain = trainRepository.save(new Train(
                UUID.randomUUID(),
                "COACH-TRAIN-3",
                "Second Train",
                NOW
        ));

        coachRepository.save(new Coach(
                UUID.randomUUID(),
                firstTrain,
                "R1",
                TravelClass.SECOND_CLASS,
                CoachReservationMode.RESERVED,
                NOW
        ));

        coachRepository.save(new Coach(
                UUID.randomUUID(),
                secondTrain,
                "R1",
                TravelClass.SECOND_CLASS,
                CoachReservationMode.RESERVED,
                NOW
        ));

        coachRepository.flush();

        assertThat(
                coachRepository
                        .existsByTrainIdAndCoachNumberIgnoreCase(
                                firstTrain.getId(),
                                "R1"
                        )
        ).isTrue();

        assertThat(
                coachRepository
                        .existsByTrainIdAndCoachNumberIgnoreCase(
                                secondTrain.getId(),
                                "R1"
                        )
        ).isTrue();
    }
}
