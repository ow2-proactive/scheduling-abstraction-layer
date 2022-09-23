/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.sal.service.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ConnectionHelper {

    private ConnectionHelper() {
    }

    @SneakyThrows
    private static BufferedReader sendGetRequestAndReturnBufferedResponse(HttpURLConnection connection) {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOGGER.error("Failed : HTTP error code : {}", connection.getResponseCode());
            return null;
        }

        return new BufferedReader(new InputStreamReader((connection.getInputStream())));
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
    public static JSONArray sendGetArrayRequestAndReturnArrayResponse(URI requestUri) {
        HttpURLConnection connection = (HttpURLConnection) requestUri.toURL().openConnection();
        connection.setRequestMethod(HttpMethod.GET.toString());
        LOGGER.debug("requestUri = {}", requestUri);

        BufferedReader br = sendGetRequestAndReturnBufferedResponse(connection);

        JSONArray result = (br != null) ? new JSONArray(new JSONTokener(br)) : null;

        connection.disconnect();

        return result;
    }
}
