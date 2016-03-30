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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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

    private ExecutorService executorService = Executors.newFixedThreadPool(30);
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

    private void loadEventData() throws IOException, ExecutionException, InterruptedException {
        if (iteration % betfairConfiguration.eventDataInterval == 0) {
            List<CompletableFuture<Void>> futureList = new ArrayList<>();
            futureList.add(loadCompetitions());
            futureList.add(loadEvents());

            CompletableFuture[] completableFutures = futureList.toArray(new CompletableFuture[futureList.size()]);

            CompletableFuture.allOf(completableFutures)
                    .whenComplete((s, throwable) -> {
                        if (throwable != null) {
                            LOGGER.error("Something really bad happened", throwable);
                        }
                    }).get();
        }
    }

    private CompletableFuture<Void> loadCompetitions() throws IOException {
        List<DataMapping> categoryMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.CATEGORY);

        List<CompletableFuture<List<Competition>>> futureList = new ArrayList<>();

        for (DataMapping categoryMapping : categoryMappings) {
            CompletableFuture<List<Competition>> cFuture = betfairClient.loadCompetitions(categoryMapping.getExternalId(), executorService);
            cFuture.whenComplete((competitions, throwable) -> {
                if (competitions.size() > 0) {
                    betfairDataMappingService.processCompetitionList(categoryMapping.getExternalDescription(), competitions);
                }
            });
            futureList.add(cFuture);
        }

        CompletableFuture[] completableFutures = futureList.toArray(new CompletableFuture[futureList.size()]);
        return CompletableFuture.allOf(completableFutures)
                .whenComplete((s, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error("Something bad happened", throwable);
                    } else {
                        LOGGER.info("Competition load completed!");
                    }
                });
    }

    /**
     * For each competition mapping marked as active, load associated events.  It is necessary to load events one
     * competition at a time because Betfair do not return any indication of the event's parent, and if we want to
     * automatically generate events in the internal model, we need to now the competition to which it belongs.
     * <p>
     * This has implications as to how many competitions should be active with Betfair.  Though in any case, Befair should
     * probably not be the primary data source!
     */
    private CompletableFuture<Void> loadEvents() throws IOException {
        List<DataMapping> competitionMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.COMPETITION);

        List<CompletableFuture<List<Event>>> futureList = new ArrayList<>();

        for (DataMapping competitionMapping : competitionMappings) {
            HashSet<String> idSet = new HashSet<>();
            idSet.add(competitionMapping.getExternalId());

            CompletableFuture<List<Event>> eFuture = betfairClient.loadEvents(idSet, executorService);
            eFuture.whenComplete((eventList, throwable) -> {
                betfairDataMappingService.processEventList(competitionMapping.getInternalId(), competitionMapping.getExternalDescription(), eventList);
            });
            futureList.add(eFuture);
        }

        CompletableFuture[] completableFutures = futureList.toArray(new CompletableFuture[futureList.size()]);
        return CompletableFuture.allOf(completableFutures)
                .whenComplete((s, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error("Something bad happened", throwable);
                    } else {
                        LOGGER.info("Event load completed!");
                    }
                });
    }

    private void loadBaseData() throws IOException {
        if (iteration % betfairConfiguration.baseDataInterval == 0) {
            try {
                CompletableFuture<List<EventType>> etFuture = betfairClient.loadEventTypes(executorService);
                etFuture.whenComplete((eventTypeList, throwable) -> {
                    betfairDataMappingService.processEventTypeList(eventTypeList);
                });

                CompletableFuture<List<MarketType>> mtFuture = betfairClient.loadMarketTypes(executorService);
                mtFuture.whenComplete((marketTypeList, throwable) -> {
                    betfairDataMappingService.processMarketTypeList(marketTypeList);
                });

                etFuture.get();
                mtFuture.get();
            } catch (Exception e) {
                LOGGER.error("Failed to load event types", e);
            }
        }
    }

}
