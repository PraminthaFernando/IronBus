package com.lsf.ironbus.fare.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(FareProperties.class)
@Configuration
public class FareConfiguration {
}