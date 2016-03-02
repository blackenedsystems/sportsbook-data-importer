package com.blackenedsystems.sportsbook.data;

import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConnector;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SportsbookDataImporterApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(SportsbookDataImporterApplication.class, args);

            BetfairConnector betfairConnector = (BetfairConnector) applicationContext.getBean("betfairConnector");
            BetfairClient betfairClient = (BetfairClient) applicationContext.getBean("betfairClient");

            betfairConnector.logon();
            betfairClient.loadSports(new MarketFilter());
            betfairConnector.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

