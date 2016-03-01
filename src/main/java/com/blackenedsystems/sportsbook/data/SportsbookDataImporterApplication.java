package com.blackenedsystems.sportsbook.data;

import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SportsbookDataImporterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SportsbookDataImporterApplication.class, args);
//        BetfairClient betfairClient = (BetfairClient) applicationContext.getBean("betfairClient");
//        try {
//            betfairClient.logon();
//            betfairClient.logout();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
