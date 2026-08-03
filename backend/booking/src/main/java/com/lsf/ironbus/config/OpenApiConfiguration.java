package com.lsf.ironbus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfiguration {

    private final OpenApiProperties properties;

    @Bean
    public OpenAPI ironBusOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(properties.title())
                        .description(properties.description())
                        .version(properties.version())
                        .contact(new Contact()
                                .name("IronBus Engineering"))
                        .license(new License()
                                .name("Assessment Project")))
                .servers(List.of(
                        new Server()
                                .url(properties.serverUrl())
                                .description("Configured API server")
                ));
    }
}