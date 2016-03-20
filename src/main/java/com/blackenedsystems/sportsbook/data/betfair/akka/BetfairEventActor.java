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
                            betfairDataMappingService.processEventList(pe.eventList);
                        })
                        .build()
        );
    }

    public static class ProcessEvents {
        private List<Event> eventList;

        public ProcessEvents(List<Event> eventList) {
            this.eventList = eventList;
        }
    }
}
