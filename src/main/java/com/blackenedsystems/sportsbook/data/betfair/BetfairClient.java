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

    private final BetfairConnector betfairConnector;

    @Autowired
    private BetfairConfiguration betfairConfiguration;

    @Autowired
    public BetfairClient(final BetfairConnector betfairConnector) {
        this.betfairConnector = betfairConnector;
    }

    /**
     * @return a list of eventTypes/Categories in Betfair's structure, using the default locale for strings.
     * @throws IOException
     */
    public List<EventType> loadEventTypes() throws IOException {
        return loadEventTypes(Locale.getDefault());
    }

    /**
     * @param locale locale to use for strings returned by Betfair
     * @return a list of Sports in Betfair's structure, using the supplied locale for strings.
     * @throws IOException
     */
    public List<EventType> loadEventTypes(final Locale locale) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(FILTER, new MarketFilter());
        params.put(LOCALE, locale.toString());

        String result = betfairConnector.postRequest(APIOperation.LISTEVENTTYPES, params);
        LOGGER.debug("Result of loadEventTypes: {}", result);

        List<EventTypeWrapper> etw = Arrays.asList(new ObjectMapper().readValue(result, EventTypeWrapper[].class));
        return EventTypeWrapper.extractEventTypes(etw);
    }

    /**
     * @param eventTypeId betfair eventType id, used to filter the list of competitions returned.
     * @return a list of competitions in Betfair's structure for the chosen eventType, using the default locale for strings.
     * @throws IOException
     */
    public List<Competition> loadCompetitions(final String eventTypeId) throws IOException {
        return loadCompetitions(eventTypeId, Locale.getDefault());
    }

    /**
     * @param eventTypeId betfair eventType id, used to filter the list of competitions returned.
     * @param locale      locale to use for strings returned by Betfair
     * @return a list of competitions in Betfair's structure for the chosen eventType, using the supplied locale for strings.
     * @throws IOException
     */
    public List<Competition> loadCompetitions(final String eventTypeId, final Locale locale) throws IOException {
        HashSet<String> eventTypeIdSet = new HashSet<>();
        eventTypeIdSet.add(eventTypeId);

        MarketFilter filter = new MarketFilter();
        filter.setEventTypeIds(eventTypeIdSet);

        Map<String, Object> params = new HashMap<>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale.toString());

        String result = betfairConnector.postRequest(APIOperation.LISTCOMPETITIONS, params);
        LOGGER.debug("Result of loadCompetitions: {}", result);

        List<CompetitionWrapper> cw = Arrays.asList(new ObjectMapper().readValue(result, CompetitionWrapper[].class));
        return CompetitionWrapper.extractCompetitions(cw);
    }

    /**
     * @return a list of market types used by Betfair, using the default locale for strings.
     * @throws IOException
     */
    public List<MarketType> loadMarketTypes() throws IOException {
        return loadMarketTypes(Locale.getDefault());
    }

    /**
     * @param locale locale to use for strings returned by Betfair
     * @return a list of market types used by Betfair, using the supplied locale for strings.
     * @throws IOException
     */
    private List<MarketType> loadMarketTypes(final Locale locale) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(FILTER, new MarketFilter());
        params.put(LOCALE, locale.toString());

        String result = betfairConnector.postRequest(APIOperation.LISTMARKETTYPES, params);
        LOGGER.debug("Result of loadCompetitions: {}", result);

        return Arrays.asList(new ObjectMapper().readValue(result, MarketType[].class));
    }

    /**
     * @param competitionIds the betfair competition ids for which we should load events.
     * @return  a list of events for the specified competitions, with start times after 'now', using the default locale for strings.
     */
    public List<Event> loadEvents(final Set<String> competitionIds) throws IOException {
        return loadEvents(competitionIds, Locale.getDefault());
    }

    /**
     * @param competitionIds the betfair competition ids for which we should load events.
     * @param locale         locale to use for strings returned by Betfair
     * @return  a list of events for the specified competitions, with start times after 'now'.
     */
    public List<Event> loadEvents(final Set<String> competitionIds, final Locale locale) throws IOException {
        TimeRange startTime = new TimeRange();
        startTime.setFrom(new Date());

        MarketFilter marketFilter = new MarketFilter();
        marketFilter.setCompetitionIds(competitionIds);
        marketFilter.setMarketStartTime(startTime);

        Map<String, Object> params = new HashMap<>();
        params.put(FILTER, marketFilter);
        params.put(LOCALE, locale.toString());

        String result = betfairConnector.postRequest(APIOperation.LISTEVENTS, params);
        LOGGER.debug("Result of loadEvents: {}", result);

        List<EventWrapper> eventWrappers = Arrays.asList(new ObjectMapper().readValue(result, EventWrapper[].class));
        return EventWrapper.extractEventTypes(eventWrappers);
    }
}
