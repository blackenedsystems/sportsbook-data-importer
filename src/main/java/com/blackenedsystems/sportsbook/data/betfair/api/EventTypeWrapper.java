package com.blackenedsystems.sportsbook.data.betfair.api;

import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Used when deserialising responses from Betfair.
 *
 * @author Alan Tibbetts
 * @since 2/3/16 16:10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventTypeWrapper {
    private EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public static List<EventType> extractEventTypes(final List<EventTypeWrapper> wrappers) {
        return wrappers.stream()
                .map(EventTypeWrapper::getEventType)
                .collect(Collectors.toList());
    }
}
