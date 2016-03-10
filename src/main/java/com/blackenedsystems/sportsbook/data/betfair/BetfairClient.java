package com.blackenedsystems.sportsbook.data.betfair;

import com.blackenedsystems.sportsbook.data.betfair.api.APIOperation;
import com.blackenedsystems.sportsbook.data.betfair.api.CompetitionWrapper;
import com.blackenedsystems.sportsbook.data.betfair.api.EventTypeWrapper;
import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketFilter;
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
    public BetfairClient(final BetfairConnector betfairConnector) {
        this.betfairConnector = betfairConnector;
    }

    /**
     * @return a list of Sports in Betfair's structure, using the default locale for strings.
     * @throws IOException
     */
    public List<EventType> loadSports() throws IOException {
        return loadSports(Locale.getDefault());
    }

    /**
     * @param locale locale to use for strings returned by Betfair
     * @return a list of Sports in Betfair's structure, using the supplied locale for strings.
     * @throws IOException
     */
    public List<EventType> loadSports(final Locale locale) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(FILTER, new MarketFilter());
        params.put(LOCALE, locale.toString());

        String result = betfairConnector.postRequest(APIOperation.LISTEVENTTYPES, params);
        LOGGER.debug("Result of loadSports: {}", result);

        List<EventTypeWrapper> etw = Arrays.asList(new ObjectMapper().readValue(result, EventTypeWrapper[].class));
        return EventTypeWrapper.extractEventTypes(etw);
    }

    /**
     * @param sportId betfair sport id, used to filter the list of competitions returned.
     * @return a list of competitions in Betfair's structure for the chosen sport, using the default locale for strings..
     * @throws IOException
     */
    public List<Competition> loadCompetitions(final String sportId) throws IOException {
        return loadCompetitions(sportId, Locale.getDefault());
    }

    /**
     * @param sportId betfair sport id, used to filter the list of competitions returned.
     * @param locale  locale to use for strings returned by Betfair
     * @return a list of competitions in Betfair's structure for the chosen sport, using the supplied locale for strings.
     * @throws IOException
     */
    public List<Competition> loadCompetitions(final String sportId, final Locale locale) throws IOException {
        HashSet<String> sportIdSet = new HashSet<>();
        sportIdSet.add(sportId);

        MarketFilter filter = new MarketFilter();
        filter.setEventTypeIds(sportIdSet);

        Map<String, Object> params = new HashMap<>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale.toString());

        String result = betfairConnector.postRequest(APIOperation.LISTCOMPETITIONS, params);
        LOGGER.debug("Result of loadCompetitions: {}", result);

        List<CompetitionWrapper> cw = Arrays.asList(new ObjectMapper().readValue(result, CompetitionWrapper[].class));
        return CompetitionWrapper.extractCompetitions(cw);
    }
}
