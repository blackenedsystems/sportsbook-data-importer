package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.OnSuccess;
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

import static com.blackenedsystems.sportsbook.data.betfair.akka.BetfairConnectorActor.*;

/**
 * Top level Actor for the Betfair data import process.
 *
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Named("BetfairWorkflowActor")
@Scope("prototype")
public class BetfairWorkflowActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairWorkflowActor.class);

    @Autowired
    private ActorService actorService;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Start) {
            ActorRef bfcRef = actorService.actorFromContext(context(), "BetfairConnectorActor", "betfairConnector");
            bfcRef.tell(new Connect(sender()), self());

        } else if (message instanceof Connected) {
            loadData((Connected) message);

        } else if (message instanceof Disconnected) {
            ((Disconnected) message).replyTo.tell(new Complete(), self());

        } else {
            unhandled(message);
        }
    }

    private void loadData(final Connected connectedMessage) {
        ActorRef clientActor = actorService.actorFromContext(context(), "BetfairClientActor", "betfairClient");

        Timeout timeout = new Timeout(Duration.create(30, "seconds"));
        Future<Object> future = Patterns.ask(clientActor, new BetfairClientActor.LoadSports(connectedMessage.replyTo), timeout);
        future.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object result) throws Throwable {
                if (result instanceof BetfairClientActor.DataLoaded) {
                    ActorRef bfcRef = actorService.actorFromContext(context(), "BetfairConnectorActor", "betfairConnector");
                    bfcRef.tell(new Disconnect(((BetfairClientActor.DataLoaded) result).replyTo), self());
                }
            }
        }, context().dispatcher());
    }

    public static class Start {
    }

    public static class Complete {
    }
}
