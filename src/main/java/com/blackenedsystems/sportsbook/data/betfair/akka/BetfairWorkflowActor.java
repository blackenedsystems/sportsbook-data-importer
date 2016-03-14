package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static akka.dispatch.Futures.sequence;
import static com.blackenedsystems.sportsbook.data.betfair.akka.BetfairConnectorActor.*;

/**
 * Top level Actor for the Betfair data import process.
 *
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Named("BetfairWorkflowActor")
@Scope("prototype")
public class BetfairWorkflowActor extends AbstractActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairWorkflowActor.class);

    @Autowired
    private ActorService actorService;

    @Autowired
    private BetfairConfiguration betfairConfiguration;

    public BetfairWorkflowActor() {
        receive(
                ReceiveBuilder
                        .match(Start.class, s -> {
                            ActorRef bfcRef = actorService.actorFromContext(context(), "BetfairConnectorActor", "betfairConnector");
                            bfcRef.tell(new Connect(sender()), self());
                        })
                        .match(Connected.class, this::loadData)
                        .match(Disconnected.class, d -> {
                            LOGGER.info("Disconnected from Betfair.");
                        })
                        .build()
        );
    }

    @Override
    public void preStart() throws Exception {
        getContext().system().scheduler()
                .schedule(
                        Duration.create(betfairConfiguration.initialWorkflowDelay, TimeUnit.SECONDS),
                        Duration.create(betfairConfiguration.workflowInterval, TimeUnit.SECONDS),
                        self(),
                        new Start(),
                        getContext().dispatcher(),
                        null);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        // override postRestart so we don't call preStart and schedule a new message
    }

    /**
     * Asynchronously loads all of the base data (sports, competitions, markets, etc).  When all of the data has been
     * downloaded from Betfair (but not necessarily processed) we can log out of Betfair.
     *
     * @param connectedMessage contains the replyTo Actor (for Betfair logout)
     */
    private void loadData(final Connected connectedMessage) {
        LOGGER.info("Connected to Betfair.");

        ActorRef clientActor = actorService.actorFromContext(context(), "BetfairClientActor", "betfairClient");

        Timeout timeout = new Timeout(Duration.create(10, "seconds"));

        List<Future<Object>> baseDataFutures = new ArrayList<>();
        baseDataFutures.add(createLoadSportsFuture(connectedMessage, clientActor, timeout));
        baseDataFutures.add(createLoadCompetitionsFuture(connectedMessage, clientActor, timeout));
        baseDataFutures.add(createLoadMarketTypesFuture(connectedMessage, clientActor, timeout));

        Future<Iterable<Object>> sequence = sequence(baseDataFutures, context().dispatcher());
        sequence.onComplete(new OnComplete<Iterable<Object>>() {
            @Override
            public void onComplete(Throwable throwable, Iterable<Object> objects) throws Throwable {
                if (throwable != null) {
                    LOGGER.error("Betfair load data process failed.", throwable);
                }

                ActorRef bfcRef = actorService.actorFromContext(context(), "BetfairConnectorActor", "betfairConnector");
                bfcRef.tell(new Disconnect(connectedMessage.replyTo), self());
            }
        }, context().dispatcher());
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

    private Future<Object> createLoadSportsFuture(final Connected connectedMessage, final ActorRef clientActor, final Timeout timeout) {
        Future<Object> sportFuture = Patterns.ask(clientActor, new BetfairClientActor.LoadSports(connectedMessage.replyTo), timeout);
        sportFuture.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable throwable) throws Throwable {
                if (throwable != null) {
                    LOGGER.error("Betfair load sports data process failed.", throwable);
                }

            }
        }, context().dispatcher());
        return sportFuture;
    }

    public static class Start {
    }
}
