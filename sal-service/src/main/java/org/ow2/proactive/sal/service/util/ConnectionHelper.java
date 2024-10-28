/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class ConnectionHelper {

    private ConnectionHelper() {
    }

    @SneakyThrows
    private static BufferedReader sendGetRequestAndReturnBufferedResponse(HttpURLConnection connection)
            throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            String errorMessage = "Failed: HTTP error code: " + responseCode;
            LOGGER.error(errorMessage);
            throw new IOException(errorMessage);
        }

        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    @SneakyThrows
    public static JSONObject sendGetRequestAndReturnObjectResponse(URI requestUri) {
        HttpURLConnection connection = (HttpURLConnection) requestUri.toURL().openConnection();
        connection.setRequestMethod(HttpMethod.GET.toString());
        LOGGER.debug("requestUri = {}", requestUri);

        BufferedReader br = sendGetRequestAndReturnBufferedResponse(connection);

        JSONObject result = (br != null) ? new JSONObject(new JSONTokener(br)) : null;

        connection.disconnect();

        return result;
    }

    @SneakyThrows
    public static JSONArray sendGetArrayRequestAndReturnArrayResponse(URI requestUri) throws IOException {
        HttpURLConnection connection = null;
        JSONArray result = new JSONArray();

        try {
            connection = (HttpURLConnection) requestUri.toURL().openConnection();
            connection.setRequestMethod(HttpMethod.GET.toString());
            LOGGER.debug("requestUri = {}", requestUri);

            // Check if connection is successfully established
            if (connection != null) {
                try (BufferedReader br = sendGetRequestAndReturnBufferedResponse(connection)) {
                    if (br != null) {
                        result = new JSONArray(new JSONTokener(br));
                    } else {
                        LOGGER.warn("No response received from request to {}", requestUri);
                    }
                }
            } else {
                LOGGER.error("Failed to establish connection to {}", requestUri);
            }
        } catch (IOException e) {
            LOGGER.error("IO exception occurred while making request to {}: {}", requestUri, e.getMessage(), e);
            throw e; // Rethrow IOException to be handled by the calling method
        } catch (JSONException e) {
            LOGGER.error("JSON parsing error occurred while processing response from {}: {}",
                         requestUri,
                         e.getMessage(),
                         e);
            throw new IOException("JSON parsing error occurred", e); // Wrap and throw as IOException
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while processing request to {}: {}",
                         requestUri,
                         e.getMessage(),
                         e);
            throw new IOException("Unexpected error occurred", e); // Wrap and throw as IOException
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

}
