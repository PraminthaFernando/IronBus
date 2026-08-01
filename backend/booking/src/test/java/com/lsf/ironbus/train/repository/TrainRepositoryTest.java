package com.lsf.ironbus.train.repository;

import com.lsf.ironbus.support.PostgreSqlIntegrationTest;
import com.lsf.ironbus.train.domain.Train;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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
class TrainRepositoryTest extends PostgreSqlIntegrationTest {
    @Autowired TrainRepository trainRepository;

    @Test
    void savesAndFindsActiveTrainByCode() {
        Train train = trainRepository.saveAndFlush(new Train(UUID.randomUUID(), "REPO-TRAIN-1", "Repository Train", NOW));
        assertThat(trainRepository.findByCodeIgnoreCase("repo-train-1")).contains(train);
    }

    @Test
    void preventsDuplicateTrainCode() {
        trainRepository.saveAndFlush(new Train(UUID.randomUUID(), "REPO-DUP-TRAIN", "First Train", NOW));
        assertThatThrownBy(() -> trainRepository.saveAndFlush(
                new Train(UUID.randomUUID(), "REPO-DUP-TRAIN", "Second Train", NOW)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
