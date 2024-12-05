/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service.infrastructure;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.utils.URIBuilder;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.proactive.sal.model.PACloud;
import org.ow2.proactive.sal.service.util.ConnectionHelper;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("PAConnectorIaasGateway")
public class PAConnectorIaasGateway {

    private String paURL;

    private final String CONNECTOR_IAAS_PATH = "/connector-iaas";

    /**
     * Init a gateway to the ProActive Connector IAAS
     * @param paServerURL ProActive URL (exp: https://try.activeeon.com:8443/)
     */
    public void init(String paServerURL) {
        this.paURL = paServerURL;
    }

    @SneakyThrows
    public JSONObject getNodeCandidates(String nodeSourceName, String region, String imageReq, String token) {
        Validate.notNull(nodeSourceName, "nodeSourceName must not be null");
        Validate.notNull(region, "region must not be null");
        LOGGER.info("Retrieving node candidates for cloud " + nodeSourceName + " region " + region + " and imageReq " +
                    imageReq);
        JSONObject nodeCandidates;

        URIBuilder uriBuilder = new URIBuilder(new URL(paURL).toURI());
        URI requestUri = uriBuilder.setPath(CONNECTOR_IAAS_PATH + "/infrastructures/" + nodeSourceName +
                                            "/nodecandidates")
                                   .addParameter("region", region)
                                   .addParameter("imageReq", imageReq)
                                   .build();
        if (!token.isEmpty()) {
            requestUri = uriBuilder.addParameter("nextToken", token).build();
        }

        nodeCandidates = ConnectionHelper.sendGetRequestAndReturnObjectResponse(requestUri);
        LOGGER.info("Node candidates retrieved for successfully: {}", nodeCandidates);

        return nodeCandidates;
    }

    @SneakyThrows
    public JSONArray getImages(String nodeSourceName) {
        Validate.notNull(nodeSourceName, "nodeSourceName must not be null");
        LOGGER.info("Retrieving images for cloud " + nodeSourceName);
        JSONArray images = new JSONArray();

        try {
            URIBuilder uriBuilder = new URIBuilder(new URL(paURL).toURI());
            URI requestUri = uriBuilder.setPath(CONNECTOR_IAAS_PATH + "/infrastructures/" + nodeSourceName + "/images")
                                       .build();

            images = ConnectionHelper.sendGetArrayRequestAndReturnArrayResponse(requestUri);
            if (images.isEmpty()) {
                LOGGER.info("No images found for cloud {}", nodeSourceName);
            } else {
                LOGGER.info("Images retrieved for cloud {}. Images: {}", nodeSourceName, images);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while retrieving images for cloud {}: {}",
                         nodeSourceName,
                         e.getMessage(),
                         e);
            throw new RuntimeException("Failed to retrieve images", e); // Convert to unchecked exception if necessary
        }

        return images;
    }

    @SneakyThrows
    public JSONArray getRegions(String nodeSourceName) {
        Validate.notNull(nodeSourceName, "nodeSourceName must not be null");
        LOGGER.debug("Retrieving regions for cloud " + nodeSourceName);
        JSONArray regions = null;

        URIBuilder uriBuilder = new URIBuilder(new URL(paURL).toURI());
        URI requestUri = uriBuilder.setPath(CONNECTOR_IAAS_PATH + "/infrastructures/" + nodeSourceName + "/regions")
                                   .build();

        regions = ConnectionHelper.sendGetArrayRequestAndReturnArrayResponse(requestUri);
        LOGGER.debug("Regions retrieved for cloud {}. Images: {}", nodeSourceName, regions);

        return regions;
    }

    @SneakyThrows
    public void defineInfrastructure(String infrastructureName, PACloud cloud, String region) {
        Validate.notNull(infrastructureName, "infrastructureName must not be null");
        Validate.notNull(cloud.getCloudProviderName(), "cloudProviderName must not be null");

        URIBuilder uriBuilder = new URIBuilder(new URL(paURL).toURI());
        URI requestUri = uriBuilder.setPath(CONNECTOR_IAAS_PATH + "/infrastructures").build();

        HttpURLConnection connection = (HttpURLConnection) requestUri.toURL().openConnection();
        connection.setRequestMethod(HttpMethod.POST.toString());
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setDoOutput(true);

        String jsonOutputString;
        switch (cloud.getCloudProviderName()) {
            case AWS_EC2:
                jsonOutputString = "{\"id\": \"" + infrastructureName + "\"," + "\"type\": \"" +
                                   cloud.getCloudProviderName() + "\"," + "\"credentials\": {\"username\": \"" +
                                   cloud.getCredentials().getUserName() + "\", \"password\": \"" +
                                   cloud.getCredentials().getPrivateKey() + "\"}}";
                break;
            case OPENSTACK:
                jsonOutputString = "{\"id\": \"" + infrastructureName + "\"," +
                                   "\"type\": \"openstack-nova\", \"endpoint\": \"" + cloud.getEndpoint() +
                                   "\", \"scope\":{\"prefix\": \"" + cloud.getScopePrefix() + "\", \"value\":\"" +
                                   cloud.getScopeValue() + "\"}, \"identityVersion\": \"" + cloud.getIdentityVersion() +
                                   "\", " + "\"credentials\": {\"username\": \"" +
                                   cloud.getCredentials().getUserName() + "\", \"password\": \"" +
                                   cloud.getCredentials().getPrivateKey() + "\", \"domain\": \"" +
                                   cloud.getCredentials().getDomain() + "\"}, \"region\": \"" + region + "\"}";
                break;
            case AZURE:
                jsonOutputString = "{\"id\": \"" + infrastructureName + "\"," + "\"type\": \"" +
                                   cloud.getCloudProviderName() + "\"," + "\"credentials\": {\"username\": \"" +
                                   cloud.getCredentials().getUserName() + "\", \"password\": \"" +
                                   cloud.getCredentials().getPrivateKey() + "\", \"domain\": \"" +
                                   cloud.getCredentials().getDomain() + "\", \"subscriptionId\": \"" +
                                   cloud.getCredentials().getSubscriptionId() + "\"}}";
                break;
            default:
                throw new IllegalArgumentException("The infrastructure " + infrastructureName + " is not handled yet.");
        }

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonOutputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        LOGGER.debug("requestUri = {}", requestUri);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOGGER.error("Failed : HTTP error code : {}", connection.getResponseCode());
        }

        LOGGER.debug("Infrastructure defined successfully.");

        connection.disconnect();
    }

    @SneakyThrows
    public List<String> getAllRegions(String infrastructureName) {
        Validate.notNull(infrastructureName, "infrastructureName must not be null");
        List<String> regions = null;

        URIBuilder uriBuilder = new URIBuilder(new URL(paURL).toURI());
        URI requestUri = uriBuilder.setPath(CONNECTOR_IAAS_PATH + "/infrastructures/" + infrastructureName + "/regions")
                                   .build();

        HttpURLConnection connection = (HttpURLConnection) requestUri.toURL().openConnection();
        connection.setRequestMethod(HttpMethod.GET.toString());
        LOGGER.debug("requestUri = " + requestUri.toString());

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOGGER.error("Failed : HTTP error code : " + connection.getResponseCode());
            return null;
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(connection.getInputStream(), writer, "utf-8");
        String response = writer.toString();

        response = response.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "");

        regions = Stream.of(response.split(",")).map(String::trim).collect(Collectors.toList());
        LOGGER.debug("Regions retrieved successfully: " + regions);

        connection.disconnect();

        return regions;
    }

    @SneakyThrows
    public void deleteInfrastructure(String infrastructureName) {
        Validate.notNull(infrastructureName, "infrastructureName must not be null");

        URIBuilder uriBuilder = new URIBuilder(new URL(paURL).toURI());
        URI requestUri = uriBuilder.setPath(CONNECTOR_IAAS_PATH + "/infrastructures/" + infrastructureName).build();

        HttpURLConnection connection = (HttpURLConnection) requestUri.toURL().openConnection();
        connection.setRequestMethod(HttpMethod.DELETE.toString());

        LOGGER.debug("requestUri = {}", requestUri);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOGGER.error("Failed : HTTP error code : {}", connection.getResponseCode());
        }

        LOGGER.debug("Infrastructure deleted successfully.");

        connection.disconnect();
    }
}
