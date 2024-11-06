/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.nc.NodeCandidateUtils;
import org.ow2.proactive.sal.service.nc.UpdatingNodeCandidatesUtils;
import org.ow2.proactive.sal.service.nc.WhiteListedInstanceTypesUtils;
import org.ow2.proactive.sal.service.service.infrastructure.PAConnectorIaasGateway;
import org.ow2.proactive.sal.service.service.infrastructure.PAResourceManagerGateway;
import org.ow2.proactive.sal.service.util.JCloudsInstancesUtils;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.ow2.proactive_grid_cloud_portal.scheduler.exception.PermissionRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("PersistenceService")
public class PersistenceService {
    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private CloudService cloudService;

    @Autowired
    private PAConnectorIaasGateway connectorIaasGateway;

    @Autowired
    private PAResourceManagerGateway resourceManagerGateway;

    @Autowired
    private UpdatingNodeCandidatesUtils updatingNodeCandidatesUtils;

    @Autowired
    private NodeCandidateUtils nodeCandidateUtils;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Clean all clusters, clouds, edge devices, and database entries
     * @param sessionId A valid session id
     */
    public void cleanAll(String sessionId) throws NotConnectedException {
        LOGGER.info("Received cleanAll endpoint call ....");
        // Check if the connection is active
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }

        LOGGER.info("CLEAN-ALL: Cleaning Clouds ...");
        // Delegate cloud cleanup to cleanAllClouds method
        cleanAllClouds(sessionId);
        LOGGER.info("CLEAN-ALL: Successfully cleaned all clouds.");

        LOGGER.info("CLEAN-ALL: Cleaning Clusters ...");
    }

    /**
     * Cleans all clouds by undeploying cloud nodes and removing cloud entries.
     * @param sessionId A valid session id
     */
    public void cleanAllClouds(String sessionId) throws NotConnectedException {
        // Check if the connection is active
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }

        LOGGER.info("Cleaning ALL Clouds and undeploying the nodes...");

        try {
            // Retrieve all clouds
            List<PACloud> clouds = cloudService.getAllClouds(sessionId);

            // Create a list to store the cloud IDs
            List<String> cloudIds = new ArrayList<>();
            for (PACloud cloud : clouds) {
                cloudIds.add(cloud.getCloudId());
            }

            // Pass the list of cloud IDs to the removeClouds method
            cloudService.removeClouds(sessionId, cloudIds, true);

            LOGGER.info("Successfully cleaned all clouds.");

        } catch (Exception e) {
            // Log the error with a message and stack trace
            LOGGER.error("ERROR occurred while cleaning clouds.", e);
        }

    }

}
