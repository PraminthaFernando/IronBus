package com.lsf.ironbus.segment.application;

import com.lsf.ironbus.fare.domain.Fare;
import com.lsf.ironbus.fare.domain.FarePolicy;
import com.lsf.ironbus.segment.app.command.ResolveJourneyLegCommand;
import com.lsf.ironbus.segment.app.service.JourneyLegQuoteService;
import com.lsf.ironbus.segment.infra.persistence.JourneyLegResolver;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static com.lsf.ironbus.support.Phase2Fixtures.journeyLeg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JourneyLegQuoteServiceTest {

    @Mock
    JourneyLegResolver resolver;

    @Mock
    FarePolicy farePolicy;

    JourneyLegQuoteService service;

    @BeforeEach
    void setUp() {
        service = new JourneyLegQuoteService(resolver, farePolicy);
    }

    @Test
    void returnsSegmentsDistanceAndFare() {
        var command = new ResolveJourneyLegCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        var leg = journeyLeg(0, 2, "0.00", "120.00");

        when(resolver.resolve(command)).thenReturn(leg);
        when(farePolicy.calculate(leg, TravelClass.SECOND_CLASS))
                .thenReturn(new Fare(
                        new BigDecimal("1300.00"),
                        Currency.getInstance("LKR")
                ));

        var response = service.quote(command, TravelClass.SECOND_CLASS);

        assertThat(response.segmentSequences()).containsExactly(0, 1);
        assertThat(response.distanceKm()).isEqualByComparingTo("120.00");
        assertThat(response.fareAmount()).isEqualByComparingTo("1300.00");
        assertThat(response.currency()).isEqualTo("LKR");
    }
}
