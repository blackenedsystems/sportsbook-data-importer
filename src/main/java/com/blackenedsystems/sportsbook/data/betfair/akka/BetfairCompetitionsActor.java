package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.blackenedsystems.sportsbook.data.betfair.BetfairDataMappingService;
import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 09/03/16
 */
@Named("BetfairCompetitionsActor")
@Scope("prototype")
public class BetfairCompetitionsActor extends AbstractActor {

    @Autowired
    private BetfairDataMappingService betfairDataMappingService;

    public BetfairCompetitionsActor() {
        receive(
                ReceiveBuilder
                        .match(ProcessCompetitions.class, pc -> {
                            betfairDataMappingService.processCompetitionList(pc.sport, pc.competitionList);
                        })
                        .build()
        );
    }

    public static class ProcessCompetitions {
        public final String sport;
        public final List<Competition> competitionList;

        public ProcessCompetitions(final String sport, final List<Competition> competitionList) {
            this.sport = sport;
            this.competitionList = competitionList;
        }
    }
}
