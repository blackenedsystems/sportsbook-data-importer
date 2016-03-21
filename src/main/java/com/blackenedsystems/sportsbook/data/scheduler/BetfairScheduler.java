package com.blackenedsystems.sportsbook.data.scheduler;

import akka.actor.ActorRef;
import com.blackenedsystems.sportsbook.data.akka.ActorService;
import com.blackenedsystems.sportsbook.data.betfair.akka.BetfairBaseDataActor;
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

    private ActorRef baseDataActor;

    @PostConstruct
    public void createActors() {
        baseDataActor = actorService.actorFromContext("BetfairBaseDataActor", "befairBaseDataActor");
    }

    @Scheduled(cron = "${schedule.betfair.basedata}")
    public void loadBaseData() {
        try {
            baseDataActor.tell(new BetfairBaseDataActor.Start(), baseDataActor);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }
}
