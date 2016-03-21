package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static akka.dispatch.Futures.sequence;
import static com.blackenedsystems.sportsbook.data.betfair.akka.BetfairConnectorActor.*;

/**
 * Top level Actor for the Betfair data import process.
 *
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Named("BetfairDataImportActor")
@Scope("prototype")
public class BetfairDataImportActor extends AbstractActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairDataImportActor.class);

    @Autowired
    private ActorService actorService;

    public BetfairDataImportActor() {
        receive(
                ReceiveBuilder
                        .match(Start.class, s -> {
                            if (s.processes.size() > 0) {
                                ActorRef bfcRef = actorService.actorFromContext(context(), "BetfairConnectorActor", "betfairConnector");
                                bfcRef.tell(new Connect(sender(), s.processes), self());
                            }
                        })
                        .match(Connected.class, this::loadData)
                        .match(Disconnected.class, d -> {
                            LOGGER.info("Disconnected from Betfair.");
                        })
                        .build()
        );
    }

    /**
     * Asynchronously loads all of the base data (eventTypes, competitions, markets, etc).  When all of the data has been
     * downloaded from Betfair (but not necessarily processed) we can log out of Betfair.
     *
     * @param connectedMessage contains the replyTo Actor (for Betfair logout)
     */
    private void loadData(final Connected connectedMessage) {
        LOGGER.info("Connected to Betfair.");

        ActorRef clientActor = actorService.actorFromContext(context(), "BetfairClientActor", "betfairClient");

        List<Future<Object>> baseDataFutures = constructFuturesList(connectedMessage, clientActor);

        if (baseDataFutures.size() > 0) {
            Future<Iterable<Object>> sequence = sequence(baseDataFutures, context().dispatcher());
            sequence.onComplete(new OnComplete<Iterable<Object>>() {
                @Override
                public void onComplete(Throwable throwable, Iterable<Object> objects) throws Throwable {
                    if (throwable != null) {
                        LOGGER.error("Betfair load data process failed.", throwable);
                    }

                    sendDisconnectMessage(connectedMessage);
                }
            }, context().dispatcher());
        } else {
            sendDisconnectMessage(connectedMessage);
        }
    }

    private void sendDisconnectMessage(final Connected connectedMessage) {
        ActorRef bfcRef = actorService.actorFromContext(context(), "BetfairConnectorActor", "betfairConnector");
        bfcRef.tell(new Disconnect(connectedMessage.replyTo), self());
    }

    /**
     * Construct a list of calls to be made to Betfair.
     */
    private List<Future<Object>> constructFuturesList(final Connected connectedMessage, final ActorRef clientActor) {
        Timeout timeout = new Timeout(Duration.create(10, "seconds"));

        List<Future<Object>> baseDataFutures = new ArrayList<>();

        if (connectedMessage.processes.contains(ProcessType.BASE)) {
            baseDataFutures.add(createLoadEventTypesFuture(connectedMessage, clientActor, timeout));
            baseDataFutures.add(createLoadMarketTypesFuture(connectedMessage, clientActor, timeout));
        }

        if (connectedMessage.processes.contains(ProcessType.EVENTS)) {
            baseDataFutures.add(createLoadCompetitionsFuture(connectedMessage, clientActor, timeout));
            baseDataFutures.add(createLoadEventsFuture(connectedMessage, clientActor, timeout));
        }

//        if (connectedMessage.processes.contains(ProcessType.ODDS)) {
//
//        }

        return baseDataFutures;
    }

    private Future<Object> createLoadEventsFuture(final Connected connectedMessage, final ActorRef clientActor, final Timeout timeout) {
        Future<Object> eventFuture = Patterns.ask(clientActor, new BetfairClientActor.LoadEvents(connectedMessage.replyTo), timeout);
        eventFuture.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                if (throwable != null) {
                    LOGGER.error("Betfair load events process failed.", throwable);
                }

            }
        }, context().dispatcher());
        return eventFuture;
    }

    private Future<Object> createLoadMarketTypesFuture(final Connected connectedMessage, final ActorRef clientActor, final Timeout timeout) {
        Future<Object> marketTypeFuture = Patterns.ask(clientActor, new BetfairClientActor.LoadMarketTypes(connectedMessage.replyTo), timeout);
        marketTypeFuture.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                if (throwable != null) {
                    LOGGER.error("Betfair load market types process failed.", throwable);
                }

            }
        }, context().dispatcher());
        return marketTypeFuture;
    }

    private Future<Object> createLoadCompetitionsFuture(final Connected connectedMessage, final ActorRef clientActor, final Timeout timeout) {
        Future<Object> competitionFuture = Patterns.ask(clientActor, new BetfairClientActor.LoadCompetitions(connectedMessage.replyTo), timeout);
        competitionFuture.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                if (throwable != null) {
                    LOGGER.error("Betfair load competitions data process failed.", throwable);
                }

            }
        }, context().dispatcher());
        return competitionFuture;
    }

    private Future<Object> createLoadEventTypesFuture(final Connected connectedMessage, final ActorRef clientActor, final Timeout timeout) {
        Future<Object> eventTypeFuture = Patterns.ask(clientActor, new BetfairClientActor.LoadEventTypes(connectedMessage.replyTo), timeout);
        eventTypeFuture.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                if (throwable != null) {
                    LOGGER.error("Betfair load eventType(/category) data process failed.", throwable);
                }

            }
        }, context().dispatcher());
        return eventTypeFuture;
    }

    public static class Start {
        public final List<ProcessType> processes = new ArrayList<>();

        public void process(final ProcessType processType) {
            processes.add(processType);
        }
    }
}