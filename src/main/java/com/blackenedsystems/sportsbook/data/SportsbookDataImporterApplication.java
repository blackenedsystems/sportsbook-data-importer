package com.blackenedsystems.sportsbook.data;

import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConnector;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketFilter;
import com.blackenedsystems.sportsbook.data.mapping.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.DataMappingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class})
@ComponentScan
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

            DataMappingDao dataMappingDao = (DataMappingDao) applicationContext.getBean("dataMappingDao");
            List<DataMapping> dataMappingList = dataMappingDao.loadDataMappings();
            LOGGER.info("DataMappings size: {} ", dataMappingList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

