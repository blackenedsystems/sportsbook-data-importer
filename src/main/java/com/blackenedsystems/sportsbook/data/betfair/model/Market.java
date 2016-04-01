package com.blackenedsystems.sportsbook.data.betfair.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 01/04/16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Market {

    private String marketId;
    private String marketName;
    private List<Runner> runners;
    private Event event;

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public List<Runner> getRunners() {
        return runners;
    }

    public void setRunners(List<Runner> runners) {
        this.runners = runners;
    }

    @Override
    public String toString() {
        return "Market{" +
                "marketId='" + marketId + '\'' +
                ", marketName='" + marketName + '\'' +
                ", event=" + event +
                ", runners=" + runners +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private class Event {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}
