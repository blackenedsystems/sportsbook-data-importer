package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

/**
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Named("BetfairConnectorActor")
@Scope("prototype")
public class BetfairConnectorActor extends UntypedActor {

    @Autowired
    private BetfairConnector betfairConnector;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Connect) {
            connectToBetfair((Connect) message);

        } else if (message instanceof Disconnect) {
            disconnectFromBetfair((Disconnect) message);

        } else {
            unhandled(message);
        }
    }

    private void disconnectFromBetfair(Disconnect disconnect) throws Exception {
        if (betfairConnector.isConnected()) {
            betfairConnector.logout();
        }
        sender().tell(new Disconnected(disconnect.replyTo), self());
    }

    private void connectToBetfair(Connect connect) throws Exception {
        if (!betfairConnector.isConnected()) {
            betfairConnector.logon();
        }
        sender().tell(new Connected(connect.replyTo), self());
    }

    public static class Connect {
        public ActorRef replyTo;

        public Connect(ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class Connected {
        public ActorRef replyTo;

        public Connected(ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class Disconnect {
        public ActorRef replyTo;

        public Disconnect(ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class Disconnected {
        public ActorRef replyTo;

        public Disconnected(ActorRef replyTo) {
            this.replyTo = replyTo;
        }
    }
}
