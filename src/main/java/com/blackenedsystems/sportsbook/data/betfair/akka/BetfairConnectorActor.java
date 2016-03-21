package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Named("BetfairConnectorActor")
@Scope("prototype")
public class BetfairConnectorActor extends AbstractActor {

    @Autowired
    private BetfairConnector betfairConnector;

    public BetfairConnectorActor() {
        receive(
                ReceiveBuilder
                        .match(Connect.class, this::connectToBetfair)
                        .match(Disconnect.class, this::disconnectFromBetfair)
                        .build()
        );
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
        sender().tell(new Connected(connect.replyTo, connect.processes), self());
    }

    public static class Connect {
        public ActorRef replyTo;
        public final List<ProcessType> processes;

        public Connect(ActorRef replyTo, List<ProcessType> processes) {
            this.replyTo = replyTo;
            this.processes = processes;
        }
    }

    public static class Connected {
        public final ActorRef replyTo;
        public final List<ProcessType> processes;

        public Connected(ActorRef replyTo, List<ProcessType> processes) {
            this.replyTo = replyTo;
            this.processes = processes;
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
