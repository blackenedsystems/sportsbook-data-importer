package com.blackenedsystems.sportsbook.data.betfair.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RestResponseHandler implements ResponseHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseHandler.class);

    private static final String ENCODING_UTF_8 = "UTF-8";

    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() != 200) {
            String errorReason = entity == null ? null : EntityUtils.toString(entity, ENCODING_UTF_8);
            LOGGER.error("Call to Betfair failed. Reason: {}", errorReason);
        }

        return entity == null ? null : EntityUtils.toString(entity, ENCODING_UTF_8);
    }
}
