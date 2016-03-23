package com.blackenedsystems.sportsbook.data.betfair;

import com.blackenedsystems.sportsbook.data.betfair.api.APIOperation;
import com.blackenedsystems.sportsbook.data.betfair.api.CompetitionWrapper;
import com.blackenedsystems.sportsbook.data.betfair.api.EventTypeWrapper;
import com.blackenedsystems.sportsbook.data.betfair.api.EventWrapper;
import com.blackenedsystems.sportsbook.data.betfair.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author Alan Tibbetts
 * @since 2015-06-08,  9:01 PM
 */
@Service
public class BetfairClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairClient.class);

    private static final String FILTER = "filter";
    private static final String LOCALE = "locale";
    private static final String SORT = "sort";
    private static final String MAX_RESULT = "maxResults";
    private static final String MARKET_IDS = "marketIds";
    private static final String MARKET_ID = "marketId";
    private static final String INSTRUCTIONS = "instructions";
    private static final String CUSTOMER_REF = "customerRef";
    private static final String MARKET_PROJECTION = "marketProjection";
    private static final String PRICE_PROJECTION = "priceProjection";
    private static final String MATCH_PROJECTION = "matchProjection";
    private static final String ORDER_PROJECTION = "orderProjection";

    @Autowired
    private BetfairConnector betfairConnector;

    /**
     * @return a list of eventTypes/Categories in Betfair's structure, using the default locale for strings.
     * @throws IOException
     * @param executorService
     */
    public CompletableFuture<List<EventType>> loadEventTypes(ExecutorService executorService) {
        return loadEventTypes(Locale.getDefault(), executorService);
    }

    /**
     * @param locale locale to use for strings returned by Betfair
     * @param executorService
     * @return a list of Sports in Betfair's structure, using the supplied locale for strings.
     * @throws IOException
     */
    public CompletableFuture<List<EventType>> loadEventTypes(final Locale locale, final  ExecutorService executorService) {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Loading Betfair event types.");
            List<EventType> eventTypeList = new ArrayList<>();

            Map<String, Object> params = new HashMap<>();
            params.put(FILTER, new MarketFilter());
            params.put(LOCALE, locale.toString());

            try {
                Optional<String> result = betfairConnector.postRequest(APIOperation.LISTEVENTTYPES, params);
                if (result.isPresent()) {
                    LOGGER.debug("Result of loadEventTypes: {}", result);

                    List<EventTypeWrapper> etw = Arrays.asList(new ObjectMapper().readValue(result.get(), EventTypeWrapper[].class));
                    eventTypeList = EventTypeWrapper.extractEventTypes(etw);
                }
            } catch (IOException ioe) {
                LOGGER.error("Failed to load event types from Betfair.", ioe);
            }
            return eventTypeList;
        }, executorService);
    }

    /**
     * @param eventTypeId betfair eventType id, used to filter the list of competitions returned.
     * @return a list of competitions in Betfair's structure for the chosen eventType, using the default locale for strings.
     * @throws IOException
     */
    public CompletableFuture<List<Competition>> loadCompetitions(final String eventTypeId, final ExecutorService executorService) throws IOException {
        return loadCompetitions(eventTypeId, Locale.getDefault(), executorService);
    }

    /**
     * @param eventTypeId betfair eventType id, used to filter the list of competitions returned.
     * @param locale      locale to use for strings returned by Betfair
     * @return a list of competitions in Betfair's structure for the chosen eventType, using the supplied locale for strings.
     * @throws IOException
     */
    public CompletableFuture<List<Competition>> loadCompetitions(final String eventTypeId, final Locale locale, final ExecutorService executorService) throws IOException {

        return CompletableFuture.supplyAsync( () -> {
            LOGGER.info("Loading Betfair competitions for event type: {}", eventTypeId);

            List<Competition> competitionList = new ArrayList<>();

            try {
                HashSet<String> eventTypeIdSet = new HashSet<>();
                eventTypeIdSet.add(eventTypeId);

                MarketFilter filter = new MarketFilter();
                filter.setEventTypeIds(eventTypeIdSet);

                Map<String, Object> params = new HashMap<>();
                params.put(FILTER, filter);
                params.put(LOCALE, locale.toString());

                Optional<String> result = betfairConnector.postRequest(APIOperation.LISTCOMPETITIONS, params);
                if (result.isPresent()) {
                    LOGGER.debug("Result of loadCompetitions: {}", result);
                    List<CompetitionWrapper> cw = Arrays.asList(new ObjectMapper().readValue(result.get(), CompetitionWrapper[].class));
                    competitionList = CompetitionWrapper.extractCompetitions(cw);
                }
            } catch (Exception e) {
                String errorMessage = String.format("Failed to load competitions for event type: %s", eventTypeId);
                LOGGER.error(errorMessage, e);
            }

            return competitionList;
        }, executorService);
    }

    /**
     * @return a list of market types used by Betfair, using the default locale for strings.
     * @throws IOException
     * @param executorService
     */
    public CompletableFuture<List<MarketType>> loadMarketTypes(final ExecutorService executorService) throws IOException {
        return loadMarketTypes(Locale.getDefault(), executorService);
    }

    /**
     * @param locale locale to use for strings returned by Betfair
     * @param executorService
     * @return a list of market types used by Betfair, using the supplied locale for strings.
     * @throws IOException
     */
    private CompletableFuture<List<MarketType>> loadMarketTypes(final Locale locale, final ExecutorService executorService) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Loading Betfair market types.");
            List<MarketType> marketTypeList = new ArrayList<>();

            try {
                Map<String, Object> params = new HashMap<>();
                params.put(FILTER, new MarketFilter());
                params.put(LOCALE, locale.toString());

                Optional<String> result = betfairConnector.postRequest(APIOperation.LISTMARKETTYPES, params);
                if (result.isPresent()) {
                    LOGGER.debug("Result of loadCompetitions: {}", result);
                    marketTypeList = Arrays.asList(new ObjectMapper().readValue(result.get(), MarketType[].class));
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load market types from Betfair", e);
            }

            return marketTypeList;
        }, executorService);

    }

    /**
     * @param competitionIds the betfair competition ids for which we should load events.
     * @return  a list of events for the specified competitions, with start times after 'now', using the default locale for strings.
     */
    public CompletableFuture<List<Event>> loadEvents(final Set<String> competitionIds, final ExecutorService executorService) throws IOException {
        return loadEvents(competitionIds, Locale.getDefault(), executorService);
    }

    /**
     * @param competitionIds the betfair competition ids for which we should load events.
     * @param locale         locale to use for strings returned by Betfair
     * @return  a list of events for the specified competitions, with start times after 'now'.
     */
    public CompletableFuture<List<Event>> loadEvents(final Set<String> competitionIds, final Locale locale, final ExecutorService executorService) throws IOException {
        return CompletableFuture.supplyAsync( () -> {
            LOGGER.info("Loading Betfair events for competitions: {}", competitionIds.toArray());

            List<Event> eventList = new ArrayList<Event>();
            try {
                TimeRange startTime = new TimeRange();
                startTime.setFrom(new Date());

                MarketFilter marketFilter = new MarketFilter();
                marketFilter.setCompetitionIds(competitionIds);
                marketFilter.setMarketStartTime(startTime);

                Map<String, Object> params = new HashMap<>();
                params.put(FILTER, marketFilter);
                params.put(LOCALE, locale.toString());

                Optional<String> result = betfairConnector.postRequest(APIOperation.LISTEVENTS, params);
                if (result.isPresent()) {
                    LOGGER.debug("Result of loadEvents: {}", result);

                    List<EventWrapper> eventWrappers = Arrays.asList(new ObjectMapper().readValue(result.get(), EventWrapper[].class));
                    eventList = EventWrapper.extractEventTypes(eventWrappers);
                }
            } catch (Exception e) {
                String errorMessage = String.format("Failed to load events for competitions: %s", competitionIds.toArray());
                LOGGER.error(errorMessage, e);
            }
            return eventList;
        }, executorService);
    }
}
