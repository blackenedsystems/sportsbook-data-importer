package com.blackenedsystems.sportsbook.data.betfair.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Competition {

    private String id;
    private String name;
    private String region;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
