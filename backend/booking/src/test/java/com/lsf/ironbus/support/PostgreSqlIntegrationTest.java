package com.lsf.ironbus.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

public abstract class PostgreSqlIntegrationTest {

    protected static final PostgreSQLContainer POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer(
                "postgres:16-alpine"
        );

        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configurePostgresSql(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );

        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );

        registry.add(
                "spring.datasource.driver-class-name",
                POSTGRES::getDriverClassName
        );
    }
}