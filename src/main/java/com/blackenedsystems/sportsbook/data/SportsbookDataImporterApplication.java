package com.blackenedsystems.sportsbook.data;

import akka.actor.ActorRef;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import com.blackenedsystems.sportsbook.data.betfair.akka.BetfairWorkflowActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

@Configuration
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@ComponentScan
public class SportsbookDataImporterApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SportsbookDataImporterApplication.class);

    public static void main(String[] args) {

        try {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(SportsbookDataImporterApplication.class, args);

            ActorService actorService = (ActorService) applicationContext.getBean("actorService");
            ActorRef bfwActor = actorService.actorFromContext("BetfairWorkflowActor", "befairWorkflowActor");

            Timeout timeout = new Timeout(Duration.create(30, "seconds"));
            Future<Object> future = Patterns.ask(bfwActor, new BetfairWorkflowActor.Start(), timeout);
            future.onComplete(new OnComplete<Object>() {
                @Override
                public void onComplete(Throwable throwable, Object result) throws Throwable {
                    if (result != null && result instanceof BetfairWorkflowActor.Complete) {
                        LOGGER.debug("Got a Complete message as expected!");
                    } else {
                        LOGGER.error("Betfair workflow failed.", throwable);
                    }
                }
            }, actorService.actorSystem().dispatcher());

        } catch (Exception e) {
            LOGGER.error("Error", e);
        }


    }
}

