package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.blackenedsystems.sportsbook.data.betfair.BetfairDataMappingService;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 09/03/16
 */
@Named("BetfairEventTypesActor")
@Scope("prototype")
public class BetfairEventTypesActor extends AbstractActor {

    @Autowired
    private BetfairDataMappingService betfairDataMappingService;

    public BetfairEventTypesActor() {
        receive(
                ReceiveBuilder
                        .match(ProcessEventTypes.class, ps -> {
                            betfairDataMappingService.processEventTypeList(ps.eventTypeList);
                        })
                        .build()
        );
    }

    public static class ProcessEventTypes {
        public final List<EventType> eventTypeList;

        public ProcessEventTypes(final List<EventType> eventTypeList) {
            this.eventTypeList = eventTypeList;
        }
    }
}
