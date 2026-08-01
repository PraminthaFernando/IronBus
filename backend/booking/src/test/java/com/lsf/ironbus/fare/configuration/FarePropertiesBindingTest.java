package com.lsf.ironbus.fare.configuration;

import com.lsf.ironbus.fare.config.FareProperties;
import com.lsf.ironbus.train.enums.TravelClass;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class FarePropertiesBindingTest {

    private final ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withUserConfiguration(Config.class)
                    .withPropertyValues(
                            "app.fare.currency=LKR",
                            "app.fare.base-fare=100.00",
                            "app.fare.price-per-km=8.00",
                            "app.fare.minimum-fare=150.00",
                            "app.fare.class-multipliers.FIRST_CLASS=1.75",
                            "app.fare.class-multipliers.SECOND_CLASS=1.25",
                            "app.fare.class-multipliers.THIRD_CLASS=1.00"
                    );

    @Test
    void bindsFareProperties() {
        runner.run(context -> {
            FareProperties properties = context.getBean(FareProperties.class);

            assertThat(properties.currency()).isEqualTo("LKR");
            assertThat(properties.baseFare()).isEqualByComparingTo("100.00");
            assertThat(properties.pricePerKm()).isEqualByComparingTo("8.00");
            assertThat(properties.minimumFare()).isEqualByComparingTo("150.00");
            assertThat(properties.classMultipliers().get(TravelClass.SECOND_CLASS))
                    .isEqualByComparingTo("1.25");
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(FareProperties.class)
    static class Config {
    }
}
