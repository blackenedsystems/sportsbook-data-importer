package com.blackenedsystems.sportsbook.data;

import akka.actor.ActorRef;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@ComponentScan
public class SportsbookDataImporterApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SportsbookDataImporterApplication.class);

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(SportsbookDataImporterApplication.class, args);
        try {
            ActorService actorService = (ActorService) applicationContext.getBean("actorService");
            ActorRef bfwActor = actorService.actorFromContext("BetfairWorkflowActor", "befairWorkflowActor");
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }


    }
}

