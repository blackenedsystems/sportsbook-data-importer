package com.blackenedsystems.sportsbook.data.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Based on the Lightbend Activator sample: akka-java-spring.
 *
 * @author Alan Tibbetts
 * @since 08/03/16
 */
@Component
public class ActorService {

    private final ActorSystem actorSystem;
    private final SpringAkkaExtension springAkkaExtension;

    @Autowired
    public ActorService(final ApplicationContext applicationContext, final SpringAkkaExtension springAkkaExtension) {
        this.springAkkaExtension = springAkkaExtension;
        this.actorSystem = ActorSystem.create("SportsDataImporter");
        // initialize the application context in the Akka Spring Extension
        springAkkaExtension.get(this.actorSystem).initialize(applicationContext);
    }

    public ActorRef actorOf(final String beanName, final String actorName) {
        //TODO: find the existing actor, or create a new one ...
        return this.actorSystem.actorOf(springAkkaExtension.get(this.actorSystem).props(beanName), actorName);
    }

    public void shutdown() {
        this.actorSystem.shutdown();
        this.actorSystem.awaitTermination();
    }

    public ActorSystem actorSystem() {
        return this.actorSystem;
    }
}
