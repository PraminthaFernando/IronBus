package com.lsf.ironbus.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class CorsPropertiesValidationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(
                            TestConfiguration.class
                    );

    @Test
    void shouldFailWhenAllowedOriginsAreEmpty() {
        contextRunner
                .withPropertyValues(
                        "ironbus.cors.allowed-methods[0]=GET",
                        "ironbus.cors.allowed-headers[0]=Content-Type",
                        "ironbus.cors.exposed-headers[0]=X-Trace-Id",
                        "ironbus.cors.allow-credentials=false",
                        "ironbus.cors.max-age-seconds=3600"
                )
                .run(context -> {
                    assertThat(context).hasFailed();

                    assertThat(context.getStartupFailure())
                            .hasMessageContaining(
                                    "ironbus.cors"
                            );

                    assertThat(context.getStartupFailure())
                            .hasStackTraceContaining(
                                    "At least one allowed origin is required"
                            );
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(
            CorsProperties.class
    )
    static class TestConfiguration {
    }
}