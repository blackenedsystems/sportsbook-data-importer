package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.blackenedsystems.sportsbook.data.betfair.BetfairDataMappingService;
import com.blackenedsystems.sportsbook.data.betfair.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 2016-03-20,  3:25 PM
 */
@Named("BetfairEventActor")
@Scope("prototype")
public class BetfairEventActor extends AbstractActor {

    @Autowired
    private BetfairDataMappingService betfairDataMappingService;

    public BetfairEventActor() {
        receive(
                ReceiveBuilder
                        .match(ProcessEvents.class, pe -> {
                            betfairDataMappingService.processEventList(pe.parent, pe.eventList);
                        })
                        .build()
        );
    }

    public static class ProcessEvents {
        private final String parent;
        private final List<Event> eventList;

        public ProcessEvents(final String parent, final List<Event> eventList) {
            this.parent = parent;
            this.eventList = eventList;
        }
    }
}
