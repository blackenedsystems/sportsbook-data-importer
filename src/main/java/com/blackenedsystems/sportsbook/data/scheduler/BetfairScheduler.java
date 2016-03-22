package com.blackenedsystems.sportsbook.data.scheduler;

import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConfiguration;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConnector;
import com.blackenedsystems.sportsbook.data.betfair.BetfairDataMappingService;
import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import com.blackenedsystems.sportsbook.data.betfair.model.Event;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketType;
import com.blackenedsystems.sportsbook.data.mapping.DataMappingService;
import com.blackenedsystems.sportsbook.data.mapping.model.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.model.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.model.MappingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alan Tibbetts
 * @since 21/03/16
 */
@Component
public class BetfairScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairScheduler.class);

    @Autowired
    private BetfairConnector betfairConnector;

    @Autowired
    private BetfairClient betfairClient;

    @Autowired
    private BetfairDataMappingService betfairDataMappingService;

    @Autowired
    private BetfairConfiguration betfairConfiguration;

    @Autowired
    private DataMappingService dataMappingService;

    ExecutorService executorService = Executors.newFixedThreadPool(30);
    private int iteration = 0;

    @Scheduled(cron = "${schedule.betfair.odds}")
    public void loadData() {
        LocalDateTime start = LocalDateTime.now();
        try {
            betfairConnector.logon();
            loadBaseData();
            loadEventData();

            iteration++;
        } catch (Exception e) {
            LOGGER.error("Error", e);
        } finally {
            LOGGER.info("Betfair data load completed in {} seconds.", start.until(LocalDateTime.now(), ChronoUnit.SECONDS));

            if (betfairConnector.isConnected()) {
                try {
                    betfairConnector.logout();
                } catch (Exception e) {
                    LOGGER.error("Failed to logout from Betfair", e);
                }
            }
        }
    }

    private void loadEventData() throws IOException {
        if (iteration % betfairConfiguration.eventDataInterval == 0) {
            loadCompetitions();
            loadEvents();
        }
    }

    private void loadCompetitions() throws IOException {
        List<DataMapping> categoryMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.CATEGORY);

        for (DataMapping categoryMapping : categoryMappings) {
            List<Competition> competitions = betfairClient.loadCompetitions(categoryMapping.getExternalId());
            if (competitions.size() > 0) {
                betfairDataMappingService.processCompetitionList(categoryMapping.getExternalDescription(), competitions);
            }
        }

    }

    /**
     * For each competition mapping marked as active, load associated events.  It is necessary to load events one
     * competition at a time because Betfair do not return any indication of the event's parent, and if we want to
     * automatically generate events in the internal model, we need to now the competition to which it belongs.
     *
     * This has implications as to how many competitions should be active with Betfair.  Though in any case, Befair should
     * probably not be the primary data source!
     */
    private void loadEvents() throws IOException {
        List<DataMapping> competitionMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.COMPETITION);

        for (DataMapping competitionMapping : competitionMappings) {
            HashSet<String> idSet = new HashSet<>();
            idSet.add(competitionMapping.getExternalId());

            List<Event> eventList = betfairClient.loadEvents(idSet);
            betfairDataMappingService.processEventList(competitionMapping.getExternalDescription(), eventList);
        }
    }

    private void loadBaseData() throws IOException {
        if (iteration % betfairConfiguration.baseDataInterval == 0) {
            List<EventType> eventTypes = betfairClient.loadEventTypes();
            betfairDataMappingService.processEventTypeList(eventTypes);

            List<MarketType> marketTypes = betfairClient.loadMarketTypes();
            betfairDataMappingService.processMarketTypeList(marketTypes);
        }
    }

}
