package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

import static com.blackenedsystems.sportsbook.data.betfair.akka.BetfairConnectorActor.*;

/**
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
            ActorRef bfcRef = actorService.actorFromContext(context(), "BetfairConnectorActor", "betfairConnector");
            bfcRef.tell(new Disconnect(((Connected) message).replyTo), self());

        } else if (message instanceof Disconnected) {
            ((Disconnected) message).replyTo.tell(new Complete(), self());

        } else {
            unhandled(message);
        }
    }

    public static class Start {
    }

    ;

    public static class Complete {
    }

    ;
}
