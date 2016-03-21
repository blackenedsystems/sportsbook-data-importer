package com.blackenedsystems.sportsbook.data.scheduler;

import akka.actor.ActorRef;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import com.blackenedsystems.sportsbook.data.betfair.BetfairConfiguration;
import com.blackenedsystems.sportsbook.data.betfair.akka.BetfairDataImportActor;
import com.blackenedsystems.sportsbook.data.betfair.akka.ProcessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Alan Tibbetts
 * @since 21/03/16
 */
@Component
public class BetfairScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairScheduler.class);

    @Autowired
    private ActorService actorService;

    @Autowired
    private BetfairConfiguration betfairConfiguration;

    private ActorRef dataImportActor;
    private int iteration = 0;

    @PostConstruct
    public void createActors() {
        dataImportActor = actorService.actorFromContext("BetfairDataImportActor", "betfairDataImportActor");
    }

    @Scheduled(cron = "${schedule.betfair.odds}")
    public void loadData() {
        try {
            BetfairDataImportActor.Start msg = constructStartMessage();
            dataImportActor.tell(msg, dataImportActor);

            iteration++;
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

    private BetfairDataImportActor.Start constructStartMessage() {
        BetfairDataImportActor.Start msg = new BetfairDataImportActor.Start();
        if (betfairConfiguration.loadMarketsAndOdds) {
            msg.process(ProcessType.ODDS);
        }
        if (iteration % betfairConfiguration.eventDataInterval == 0) {
            msg.process(ProcessType.EVENTS);
        }
        if (iteration % betfairConfiguration.baseDataInterval == 0) {
            msg.process(ProcessType.BASE);
        }
        return msg;
    }
}
