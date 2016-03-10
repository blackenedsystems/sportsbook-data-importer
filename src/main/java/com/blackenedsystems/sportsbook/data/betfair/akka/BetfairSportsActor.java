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
@Named("BetfairSportsActor")
@Scope("prototype")
public class BetfairSportsActor extends AbstractActor {

    @Autowired
    private BetfairDataMappingService betfairDataMappingService;

    public BetfairSportsActor() {
        receive(
                ReceiveBuilder
                        .match(ProcessSports.class, ps -> {
                            betfairDataMappingService.processSportsList(ps.sportsList);
                        })
                        .build()
        );
    }

    public static class ProcessSports {
        public final List<EventType> sportsList;

        public ProcessSports(final List<EventType> sportsList) {
            this.sportsList = sportsList;
        }
    }
}
