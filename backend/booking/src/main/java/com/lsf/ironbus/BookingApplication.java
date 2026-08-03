package com.lsf.ironbus;

import com.lsf.ironbus.config.ApplicationProperties;
import com.lsf.ironbus.config.CorsProperties;
import com.lsf.ironbus.config.OpenApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		ApplicationProperties.class,
		CorsProperties.class,
		OpenApiProperties.class
})
public class BookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

}
