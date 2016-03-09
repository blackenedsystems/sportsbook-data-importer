package com.blackenedsystems.sportsbook.data.akka;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import scala.Option;

import javax.annotation.PreDestroy;

/**
 * Based on the Lightbend Activator sample: akka-java-spring.
 *
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Component
public class ActorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorService.class);

    private final ActorSystem actorSystem;
    private final SpringAkkaExtension springAkkaExtension;

    @Autowired
    public ActorService(final ApplicationContext applicationContext, final SpringAkkaExtension springAkkaExtension) {
        this.springAkkaExtension = springAkkaExtension;
        this.actorSystem = ActorSystem.create("SportsDataImporter");
        // initialize the application context in the Akka Spring Extension
        springAkkaExtension.get(this.actorSystem).initialize(applicationContext);

        LOGGER.info("SportsDataImporter ActorSystem initialised.");
    }

    @PreDestroy
    public void shutdown() {
        this.actorSystem.shutdown();
        this.actorSystem.awaitTermination();

        LOGGER.info("SportsDataImporter ActorSystem shutdown.");
    }

    public ActorSystem actorSystem() {
        return this.actorSystem;
    }

    /**
     * Creates (or loads) and actor from the top level of the ActorSytem.  Each data integration should have one (and
     * only one) top level actor.
     *
     * @param beanName  Spring bean name.
     * @param actorName Actor name.
     * @return a reference to a new or pre-existing Actor.
     */
    public ActorRef actorFromContext(final String beanName, final String actorName) {
        return this.actorSystem.actorOf(springAkkaExtension.get(this.actorSystem).props(beanName), actorName);
    }

    /**
     * Creates or loads a child actor using the given context.
     *
     * @param context   The actor context from which to load an existing actor, or where to create a new actor.
     * @param beanName  Spring bean name
     * @param actorName Actor name
     * @return A reference to an existing or new Actor.
     */
    public ActorRef actorFromContext(final ActorContext context, final String beanName, final String actorName) {
        Option<ActorRef> child = context.child(actorName);
        if (child.isDefined()) {
            return child.get();
        } else {
            return context.actorOf(springAkkaExtension.get(this.actorSystem).props(beanName), actorName);
        }
    }
}
