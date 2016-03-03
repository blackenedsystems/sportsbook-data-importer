# Configuration

## Build Configuration



## Data Source Property Templates

The folder: 'properties', contains template configuration files for the various data services.  
 
### Betfair

There are a number of steps to complete with Betfair before you can use this application to extract data via their api.  Full documentation
can be found in the Exchange API section of their [developers website](https://developer.betfair.com/).  However, in summary you will need to:

* Create a betfair account
* Create an application key
* Create a self-signed certificate (as described in the non-interactive login section)
* Link the self-signed certificate to your betfair account.

In addition, if this application is to be used in a commercial setting, there are other requirements you must fulfill.  Full details are on the Betfair website.

__Standard Properties__

* __betfair.login.url__ : URL to use when logging in to the Betfair API. 
* __betfair.logout.url__ : URL to use when logging out of the Betfair API.
* __betfair.exchange.api.url__ : URL used to access data from Betfair.
* __betfair.connection.timeout__ : Timeout period when connection to Betfair.
* __betfair.socket.timeout__ : Timeout period when reading data from Berfair.

__User / Environment Specific Properties__

* __betfair.login.cert__ : path to the certificate used during api authentication. 
* __betfair.login.cert.pwd__ : password required to read certificate from its local storage. 

* __betfair.api.user__ : betfair api username 
* __betfair.api.password__ : betfair api password  
* __betfair.api.key__ : betfair api key 

 