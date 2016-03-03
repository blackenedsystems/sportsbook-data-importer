package com.blackenedsystems.sportsbook.data.betfair;

import com.blackenedsystems.sportsbook.data.betfair.api.APIOperation;
import com.blackenedsystems.sportsbook.data.betfair.api.EventTypeWrapper;
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

    public List<EventType> loadSports(final MarketFilter filter) throws IOException {
        return loadSports(filter, Locale.getDefault());
    }

    public List<EventType> loadSports(final MarketFilter filter, final Locale locale) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(FILTER, filter);
        params.put(LOCALE, locale.toString());

        String result = betfairConnector.postRequest(APIOperation.LISTEVENTTYPES, params);
        LOGGER.debug("Result of loadSports: {}", result);

        List<EventTypeWrapper> etw = Arrays.asList(new ObjectMapper().readValue(result, EventTypeWrapper[].class));
        return EventTypeWrapper.extractEventTypes(etw);
    }

}
