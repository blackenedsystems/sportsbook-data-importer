package com.blackenedsystems.sportsbook.data.betfair.api;

/**
 * @author Alan Tibbetts
 * @since 2/3/16 14:08
 */
public enum APIOperation {
    LISTEVENTTYPES("listEventTypes"),
    LISTCOMPETITIONS("listCompetitions"),
    LISTTIMERANGES("listTimeRanges"),
    LISTEVENTS("listEvents"),
    LISTMARKETTYPES("listMarketTypes"),
    LISTCOUNTRIES("listCountries"),
    LISTVENUES("listVenues"),
    LISTMARKETCATALOGUE("listMarketCatalogue"),
    LISTMARKETBOOK("listMarketBook");

    private String operationName;

    private APIOperation(final String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }
}
