# Roadmap

The current 'project plan' can be found in Trello: https://trello.com/b/7zh2WCe5/sportsbook-data-importer.  Bugs and other
issues will be recorded in GitHub.

The high level roadmap for the project looks like this:

### Recently completed

* Create a Spring Boot based service that will:
  * Connect to various data providers.
    * Betfair Implementation
    * Map base data: sports, competitions, etc to internal data.
    * Load events from Betfair for specified competitions.
    * Connect events to the internal structures.
    * Provide a REST api to access various internal structures.

### Apr 2016

* Betfair Implementation
  * Periodically load markets and odds for specified Events.
* GUI
  * Map base data, such as sports and competitions to internal structures.
* Deployment
  * Deploy the service to AWS
* Integrate with Blackened sportsbook
  * Publish the events, etc using the internal data structures via AMQP (Rabbit?). 
