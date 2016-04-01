package com.blackenedsystems.sportsbook.data.scheduler;

import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConfiguration;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConnector;
import com.blackenedsystems.sportsbook.data.betfair.BetfairDataMappingService;
import com.blackenedsystems.sportsbook.data.betfair.model.*;
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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
            loadEventMarkets();

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

    /**
     * Loads sport, market types and other base data from Betfair.  This data should not be updated very often at all, this
     * data could be fetched once per week.
     */
    private void loadBaseData() {
        if (iteration % betfairConfiguration.baseDataInterval == 0) {
            try {
                CompletableFuture<List<EventType>> etFuture = betfairClient.asyncLoadEventTypes(executorService);
                etFuture.whenComplete((eventTypeList, throwable) -> {
                    betfairDataMappingService.processEventTypeList(eventTypeList);
                });

                CompletableFuture<List<MarketType>> mtFuture = betfairClient.asyncLoadMarketTypes(executorService);
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

    /**
     * Loads event market and odds data from Betfair. This occurs on every iteration of the job - the odds are the
     * data that changes most frequently.
     *
     * NB. Betfair have a limit on the amount of data that can be retrieved on each call.  Therefore, we request markets
     * for all events associated with a single competition, and only for those markets we've marked as active.  We make
     * one call per active competition.
     */
    private void loadEventMarkets() throws IOException, ExecutionException, InterruptedException {
        List<CompletableFuture<List<Market>>> futureList = new ArrayList<>();

        //TODO: connect active markets to competitions or categories, e.g. don't ask for tennis markets when dealing with a soccer competition.
        Set<String> marketTypes = loadActiveMarketTypes();

        List<DataMapping> competitionMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.COMPETITION);
        for (DataMapping competitionMapping : competitionMappings) {
            List<DataMapping> eventMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.EVENT, competitionMapping.getExternalDescription());

            Set<String> eventIdList = eventMappings.stream()
                    .map(DataMapping::getExternalId)
                    .collect(Collectors.toSet());

            if (!eventIdList.isEmpty()) {
                futureList.add(betfairClient.asyncLoadMarkets(eventIdList, marketTypes, executorService));
            }
        }

        CompletableFuture[] completableFutures = futureList.toArray(new CompletableFuture[futureList.size()]);
        CompletableFuture.allOf(completableFutures)
                .whenComplete((s, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error("Something bad happened", throwable);
                    } else {
                        LOGGER.info("Event Market load completed!");
                    }
                })
                .get();
    }

    private Set<String> loadActiveMarketTypes() {
        List<DataMapping> marketTypeMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.MARKET_TYPE);
        return marketTypeMappings.stream()
                .map(DataMapping::getExternalId)
                .collect(Collectors.toSet());
    }

    /**
     * Load competition and event data from Betfair.  This data changes infrequently, therefore data should be retrieved
     * no more than a few times per day; once per day would probably be sufficient.
     */
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
            CompletableFuture<List<Competition>> cFuture = betfairClient.asyncLoadCompetitions(categoryMapping.getExternalId(), executorService);
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

            CompletableFuture<List<Event>> eFuture = betfairClient.asyncLoadEvents(idSet, executorService);
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


}
