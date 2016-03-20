package com.blackenedsystems.sportsbook.data.betfair.api;

import com.blackenedsystems.sportsbook.data.betfair.model.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

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
        List<Event> eventTypeList = new ArrayList<>(wrappers.size());
        for (EventWrapper wrapper : wrappers) {
            eventTypeList.add(wrapper.getEvent());
        }
        return eventTypeList;
    }
}
