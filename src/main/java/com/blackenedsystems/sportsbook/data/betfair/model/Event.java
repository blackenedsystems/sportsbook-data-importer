package com.blackenedsystems.sportsbook.data.betfair.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * @author Alan Tibbetts
 * @since 2016-03-20,  3:18 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    private String id;
    private String name;
    private Date openDate;
    private String timezone;

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

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", openDate=" + openDate +
                ", timezone='" + timezone + '\'' +
                '}';
    }
}
