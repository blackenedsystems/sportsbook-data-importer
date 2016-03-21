package com.blackenedsystems.sportsbook.data.betfair.api;

import com.blackenedsystems.sportsbook.data.betfair.model.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alan Tibbetts
 * @since 2016-03-20,  3:43 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventWrapper {

    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public static List<Event> extractEventTypes(final List<EventWrapper> wrappers) {
        return wrappers.stream()
                .map(EventWrapper::getEvent)
                .collect(Collectors.toList());
    }
}
