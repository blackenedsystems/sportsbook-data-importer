package com.blackenedsystems.sportsbook.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alan Tibbetts
 * @since 21/03/16
 */

@ComponentScan
@Configuration
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@EnableCaching
public class TestSportsbookDataImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSportsbookDataImporterApplication.class, args);
    }
}
