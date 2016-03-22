package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.model.Competition;
import com.blackenedsystems.sportsbook.data.betfair.model.Event;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketType;
import com.blackenedsystems.sportsbook.data.mapping.DataMappingService;
import com.blackenedsystems.sportsbook.data.mapping.model.DataMapping;
import com.blackenedsystems.sportsbook.data.mapping.model.ExternalDataSource;
import com.blackenedsystems.sportsbook.data.mapping.model.MappingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 09/03/16
 */
@Named("BetfairClientActor")
@Scope("prototype")
public class BetfairClientActor extends AbstractActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairClientActor.class);

    @Autowired
    private ActorService actorService;

    @Autowired
    private BetfairClient betfairClient;

    @Autowired
    private DataMappingService dataMappingService;

    public BetfairClientActor() {
        receive(
                ReceiveBuilder
                        .match(LoadEventTypes.class, this::loadEventTypes)
                        .match(LoadCompetitions.class, this::loadCompetitions)
                        .match(LoadMarketTypes.class, this::loadMarketTypes)
                        .match(LoadEvents.class, this::loadEvents)
                        .build()
        );
    }

    /**
     * For each competition mapping marked as active, load associated events.  It is necessary to load events one
     * competition at a time because Betfair do not return any indication of the event's parent, and if we want to
     * automatically generate events in the internal model, we need to now the competition to which it belongs.
     *
     * This has implications as to how many competitions should be active with Betfair.  Though in any case, Befair should
     * probably not be the primary data source!
     */
    private void loadEvents(final LoadEvents le) throws IOException {
        List<DataMapping> competitionMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.COMPETITION);

        // TODO: Parallelise these calls.
        for (DataMapping competitionMapping : competitionMappings) {
            HashSet<String> idSet = new HashSet<>();
            idSet.add(competitionMapping.getExternalId());

            List<Event> eventList = betfairClient.loadEvents(idSet);

            ActorRef eventActor = actorService.actorFromContext(context(), "BetfairEventActor", "betfairEvents-" + competitionMapping.getExternalId());
            eventActor.tell(new BetfairEventActor.ProcessEvents(competitionMapping.getExternalDescription(), eventList), self());
        }

        sender().tell(new DataLoaded(le.replyTo), self());
    }

    /**
     * Load a list of market types (match odds, handicap, etc) from betfair.
     *
     * @param lmt original message for replyTo destination
     * @throws IOException
     */
    private void loadMarketTypes(final LoadMarketTypes lmt) throws IOException {
        List<MarketType> marketTypes = betfairClient.loadMarketTypes();

        ActorRef marketTypesActor = actorService.actorFromContext(context(), "BetfairMarketTypesActor", "betfairMarketTypes");
        marketTypesActor.tell(new BetfairMarketTypesActor.ProcessMarketTypes(marketTypes), self());

        sender().tell(new DataLoaded(lmt.replyTo), self());
    }

    /**
     * Load the list of betfair eventType/category data mappings that have been marked with load_children equal to true.  For each of
     * those send a message to a CompetitionActor for further processing.
     *
     * @param lc original message for replyTo destination
     * @throws IOException
     */
    private void loadCompetitions(final LoadCompetitions lc) throws IOException {
        List<DataMapping> categoryMappings = dataMappingService.loadDataMappingsMarkedForProcessing(ExternalDataSource.BETFAIR, MappingType.CATEGORY);

        for (DataMapping categoryMapping : categoryMappings) {
            List<Competition> competitions = betfairClient.loadCompetitions(categoryMapping.getExternalId());
            if (competitions.size() > 0) {
                // We'll have one Competition Actor per category.
                ActorRef competitionActor = actorService.actorFromContext(
                        context(), "BetfairCompetitionsActor", "betfairCompetitions-" + categoryMapping.getExternalId());
                competitionActor.tell(new BetfairCompetitionsActor.ProcessCompetitions(categoryMapping.getExternalDescription(), competitions), self());
            }
        }

        sender().tell(new DataLoaded(lc.replyTo), self());
    }

    /**
     * Load a list of eventTypes (/categories) from betfair and add them to our data_mapping table (if not already there).
     *
     * @param loadEventTypes original message for replyTo destination
     * @throws IOException
     */
    private void loadEventTypes(final LoadEventTypes loadEventTypes) throws IOException {
        List<EventType> eventTypeList = betfairClient.loadEventTypes();

        ActorRef eventTypesActor = actorService.actorFromContext(context(), "BetfairEventTypesActor", "betfairEventTypes");
        eventTypesActor.tell(new BetfairEventTypesActor.ProcessEventTypes(eventTypeList), self());

        sender().tell(new DataLoaded(loadEventTypes.replyTo), self());
    }

    public static class LoadEventTypes {
        public ActorRef replyTo;

        public LoadEventTypes(final ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class LoadCompetitions {
        public ActorRef replyTo;

        public LoadCompetitions(final ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class LoadMarketTypes {
        public ActorRef replyTo;

        public LoadMarketTypes(final ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class LoadEvents {
        public ActorRef replyTo;

        public LoadEvents(final ActorRef replyTo) {
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
