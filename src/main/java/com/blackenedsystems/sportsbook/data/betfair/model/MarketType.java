package com.blackenedsystems.sportsbook.data.betfair.model;

/**
 * @author Alan Tibbetts
 * @since 14/03/16
 */
public class MarketType {

    private String marketType;
    private int marketCount;

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public int getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(int marketCount) {
        this.marketCount = marketCount;
    }
}
