package fr.aeris.permalink.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = {"fr.aeris.permalink"} )
public class PermalinkRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PermalinkRestApplication.class, args);
	}
	
	
	
}


