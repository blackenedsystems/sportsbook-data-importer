package com.blackenedsystems.sportsbook.data.betfair;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Alan Tibbetts
 * @since 2015-05-19,  9:54 PM
 */
@Configuration
@PropertySource("${betfair.properties.filename}")
public class BetfairConfiguration {

    @Value("${betfair.login.url}")
    public String loginUrl;

    @Value("${betfair.logout.url}")
    public String logoutUrl;

    @Value("${betfair.login.cert}")
    public String loginCertFileName;

    @Value("${betfair.login.cert.password}")
    public String loginCertPassword;

    @Value("${betfair.api.user}")
    public String username;

    @Value("${betfair.api.password}")
    public String password;

    @Value("${betfair.api.key}")
    public String apiKey;

    @Value("${betfair.exchange.api.url}")
    public String exchangeApiUrl;

    @Value("${betfair.connection.timeout}")
    public int connectionTimeout;

    @Value("${betfair.socket.timeout}")
    public int socketTimeout;

    @Override
    public String toString() {
        return "BetfairConfiguration{" +
                "loginUrl='" + loginUrl + '\'' +
                ", logoutUrl='" + logoutUrl + '\'' +
                ", loginCertFileName='" + loginCertFileName + '\'' +
                ", loginCertPassword='****'" +
                ", exchangeApiUrl='" + exchangeApiUrl + '\'' +
                ", connectionTimeout=" + connectionTimeout +
                ", socketTimeout=" + socketTimeout +
                ", username='" + username + '\'' +
                ", password='****'" +
                ", apiKey='****'" +
                '}';
    }
}
