package com.blackenedsystems.sportsbook.data.betfair.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import com.blackenedsystems.sportsbook.data.betfair.BetfairClient;
import com.blackenedsystems.sportsbook.data.betfair.model.EventType;
import com.blackenedsystems.sportsbook.data.betfair.model.MarketFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 09/03/16
 */
@Named("BetfairClientActor")
@Scope("prototype")
public class BetfairClientActor extends UntypedActor {

    @Autowired
    private ActorService actorService;

    @Autowired
    private BetfairClient betfairClient;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof LoadSports) {
            List<EventType> sportsList = betfairClient.loadSports(new MarketFilter());

            ActorRef sportsActor = actorService.actorFromContext(context(), "BetfairSportsActor", "betfairSports");
            sportsActor.tell(new BetfairSportsActor.ProcessSports(sportsList), self());

            sender().tell(new DataLoaded(((LoadSports) message).replyTo), self());

        } else {
            unhandled(message);
        }
    }

    public static class LoadSports {
        public ActorRef replyTo;

        public LoadSports(final ActorRef replyTo) {
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
