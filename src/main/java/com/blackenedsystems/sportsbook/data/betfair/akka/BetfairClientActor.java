package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.mapping.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.DataMappingService;
import com.blackenedsystems.sportsbook.data.mapping.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.MappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.IOException;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 09/03/16
 */
@Named("BetfairClientActor")
@Scope("prototype")
public class BetfairClientActor extends AbstractActor {

    @Autowired
    private ActorService actorService;

    @Autowired
    private BetfairClient betfairClient;

    @Autowired
    private DataMappingService dataMappingService;

    public BetfairClientActor() {
        receive(
                ReceiveBuilder
                        .match(LoadSports.class, this::loadSports)
                        .match(LoadCompetitions.class, this::loadCompetitions)
                        .build()
        );
    }

    /**
     * Load the list of betfair sport data mappings that have been marked with load_children equal to true.  For each of
     * those send a message to the
     *
     * @param lc original message for replyTo destination
     * @throws IOException
     */
    private void loadCompetitions(LoadCompetitions lc) throws IOException {
        List<DataMapping> sportMappings = dataMappingService.loadDataMappingsWithLoadChildrenSet(ExternalDataSource.BETFAIR, MappingType.SPORT);

        for (DataMapping sportMapping : sportMappings) {
            List<Competition> competitions = betfairClient.loadCompetitions(sportMapping.getExternalId());
            if (competitions.size() > 0) {
                // We'll have one Competition Actor per sport.
                ActorRef competitionActor = actorService.actorFromContext(
                        context(), "BetfairCompetitionsActor", "betfairCompetitions-" + sportMapping.getExternalId());
                competitionActor.tell(new BetfairCompetitionsActor.ProcessCompetitions(sportMapping.getExternalDescription(), competitions), self());
            }
        }

        sender().tell(new DataLoaded(lc.replyTo), self());
    }

    /**
     * Load a list of sports from betfair and add them to our data_mapping table (if not already there).
     *
     * @param ls original message for replyTo destination
     * @throws IOException
     */
    private void loadSports(LoadSports ls) throws IOException {
        List<EventType> sportsList = betfairClient.loadSports();

        ActorRef sportsActor = actorService.actorFromContext(context(), "BetfairSportsActor", "betfairSports");
        sportsActor.tell(new BetfairSportsActor.ProcessSports(sportsList), self());

        sender().tell(new DataLoaded(ls.replyTo), self());
    }

    public static class LoadSports {
        public ActorRef replyTo;

        public LoadSports(final ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class LoadCompetitions {
        public ActorRef replyTo;

        public LoadCompetitions(final ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class DataLoaded {
        public ActorRef replyTo;

        public DataLoaded(final ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }
}
