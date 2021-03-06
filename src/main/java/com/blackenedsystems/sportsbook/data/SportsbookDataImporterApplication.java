package com.blackenedsystems.sportsbook.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@Configuration
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@EnableCaching
@EnableScheduling
public class SportsbookDataImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportsbookDataImporterApplication.class, args);
    }
}

