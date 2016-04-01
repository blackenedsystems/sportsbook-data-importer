# Sportsbook Data Importer

There are a number of services across the internet that provide data for use in sports betting applications.  This 
module provides functionality for fetching data from some of those services.  In addition, it provides functionality 
for integrating this data with Blackened System's sportsbook module, and provides well-defined expansion points for 
adapting this logic for other sports betting applications.

## Roadmap

See [here](./roadmap.md) 

## Configuraton

As an open source project, it would not be too clever to check in passwords, ssl certificates and the like, therefore, 
a large percentage of configuration for this project is stored in external files.   Full details can be
found [here](./configuration.md).

## Building

Various properties required by Maven should be stored in external files, e.g. database properties, etc.  These should be 
collected into a single file (see the template in the properties directory) and passed to maven via a system property, e.g.
 mvn clean install -Dexternal.properties.file=/var/lib/sportsbook/data/environment.properties.

## Betfair

