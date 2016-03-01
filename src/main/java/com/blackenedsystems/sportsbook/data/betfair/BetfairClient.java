package com.blackenedsystems.sportsbook.data.betfair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan Tibbetts
 * @since 2015-06-08,  9:01 PM
 */
@Service
public class BetfairClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetfairClient.class);

    private static int port = 443;

    @Autowired
    private BetfairConfiguration betfairConfiguration;

    private DefaultHttpClient httpClient = new DefaultHttpClient();
    private String sessionToken;

    @PostConstruct
    public void logon() throws Exception {
        //TODO: tidy this up, remove deprecated code, etc

        SSLContext ctx = SSLContext.getInstance("TLS");
        KeyManager[] keyManagers =
                getKeyManagers("pkcs12", new FileInputStream(new File(betfairConfiguration.loginCertFileName)), betfairConfiguration.loginCertPassword);
        ctx.init(keyManagers, null, new SecureRandom());
        SSLSocketFactory factory = new SSLSocketFactory(ctx, new StrictHostnameVerifier());

        ClientConnectionManager manager = httpClient.getConnectionManager();
        manager.getSchemeRegistry().register(new Scheme("https", port, factory));

        HttpPost httpPost = new HttpPost(betfairConfiguration.loginUrl);
        List<NameValuePair> nvpList = new ArrayList<>();
        nvpList.add(new BasicNameValuePair("username", betfairConfiguration.username));
        nvpList.add(new BasicNameValuePair("password", betfairConfiguration.password));

        httpPost.setEntity(new UrlEncodedFormEntity(nvpList));
        httpPost.setHeader("X-Application", betfairConfiguration.apiKey);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseString);
            if ("SUCCESS".equals(jsonNode.get("loginStatus").asText())) {
                sessionToken = jsonNode.get("sessionToken").asText();
                LOGGER.info("Successfully logged in to betfair api");
            } else {
                LOGGER.error("Failed to logon to betfair api: {}", jsonNode.get("loginStatus").asText());
            }
        }
    }

    @PreDestroy
    public void logout() throws Exception {
        HttpPost httpPost = new HttpPost(betfairConfiguration.logoutUrl);

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("X-Application", betfairConfiguration.apiKey);
        httpPost.setHeader("X-Authentication", sessionToken);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            LOGGER.info(responseString);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseString);
            if ("SUCCESS".equals(jsonNode.get("status").asText())) {
                sessionToken = null;
                LOGGER.info("Successfully logged out to betfair api");
            } else {
                LOGGER.error("Failed to logout of betfair api: {}", jsonNode.get("error").asText());
            }
        }
    }

    public void getCountries() throws Exception {

    }

    private KeyManager[] getKeyManagers(final String keyStoreType, final InputStream keyStoreFile, final String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }
}
