# Roadmap

The current 'project plan' can be found in Trello: https://trello.com/b/7zh2WCe5/sportsbook-data-importer.  Bugs and other
issues will be recorded in GitHub.

The high level roadmap for the project looks like this:

### Mar 2016

* Create a Spring Boot based service that will:
 * Connect to various data providers.
   * Betfair Implementation
     * Map base data: sports, competitions, etc to internal data.
     * Periodically load events (and odds) for specified Competitions.
 * Map base data, such as sports and competitions to internal structures via a GUI.
 * Create events (and odds) associated with the various competitions and connect them to the internal structures. 
 * Provide a REST api to access current event data.
* Deployment
 * Deploy the service to AWS
* Integrate with Blackened sportsbook
 * Publish the events, etc using the internal data structures via AMQP (Rabbit?). 
