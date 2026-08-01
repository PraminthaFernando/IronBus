package com.lsf.ironbus.train.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.lsf.ironbus.support.Phase1BFixtures.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainTest {

    @Test
    void normalizesTrainCodeAndName() {
        Train train = new Train(UUID.randomUUID(), "  udr-001  ", "  Udarata Menike  ", NOW);
        assertThat(train.getCode()).isEqualTo("UDR-001");
        assertThat(train.getName()).isEqualTo("Udarata Menike");
        assertThat(train.isActive()).isTrue();
    }

    @Test
    void rejectsBlankCode() {
        assertThatThrownBy(() -> new Train(UUID.randomUUID(), " ", "Test Train", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Train code is required");
    }

    @Test
    void canRenameAndDeactivateTrain() {
        Train train = new Train(UUID.randomUUID(), "T-001", "Original", NOW);
        train.rename("Updated", NOW.plusSeconds(10));
        train.deactivate(NOW.plusSeconds(20));
        assertThat(train.getName()).isEqualTo("Updated");
        assertThat(train.isActive()).isFalse();
        assertThat(train.getUpdatedAt()).isEqualTo(NOW.plusSeconds(20));
    }
}
