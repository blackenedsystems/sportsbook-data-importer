package com.blackenedsystems.sportsbook.data;

import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConnector;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class SportsbookDataImporterApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SportsbookDataImporterApplication.class);

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(SportsbookDataImporterApplication.class, args);

            BetfairConnector betfairConnector = (BetfairConnector) applicationContext.getBean("betfairConnector");
            BetfairClient betfairClient = (BetfairClient) applicationContext.getBean("betfairClient");

            betfairConnector.logon();
            List<EventType> eventTypes = betfairClient.loadSports(new MarketFilter());
            for (EventType eventType : eventTypes) {
                LOGGER.debug(eventType.toString());
            }
            betfairConnector.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

