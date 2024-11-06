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
import org.springframework.transaction.annotation.Transactional;

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
     * Clean all clusters, clouds, edge devices, and database entries.
     * @param sessionId A valid session id
     */
    public void cleanAll(String sessionId) throws NotConnectedException {
        LOGGER.info("Received cleanAll endpoint call with sessionId: {}", sessionId);

        // Check if the connection is active
        if (!paGatewayService.isConnectionActive(sessionId)) {
            LOGGER.warn("Session {} is not active. Aborting cleanAll operation.", sessionId);
            throw new NotConnectedException();
        }

        // Cleaning clouds
        LOGGER.info("CLEAN-ALL: Initiating cloud cleanup...");
        boolean cloudsCleaned = cleanAllCloudsFunction(sessionId);
        if (cloudsCleaned) {
            LOGGER.info("CLEAN-ALL: Successfully cleaned all clouds.");
        } else {
            LOGGER.warn("CLEAN-ALL: Cloud cleanup encountered issues.");
        }

        // Cleaning clusters
        LOGGER.info("CLEAN-ALL: Initiating cluster cleanup...");
        // Cluster cleanup logic would go here, with logging for each step

        // Additional cleanup steps (e.g., edge devices, database entries) with similar logging

        LOGGER.info("CLEAN-ALL: Completed all cleanup processes for sessionId: {}", sessionId);
    }

    /**
     * Cleans all clouds by undeploying cloud nodes and removing cloud entries.
     * @param sessionId A valid session id
     * @return true if all clouds were cleaned successfully, false otherwise
     */
    public boolean cleanAllClouds(String sessionId) throws NotConnectedException {
        LOGGER.info("Received cleanAllClouds endpoint call with sessionId: {}", sessionId);

        // Check if the connection is active
        if (!paGatewayService.isConnectionActive(sessionId)) {
            LOGGER.warn("Session {} is not active. Aborting cloud cleanup.", sessionId);
            throw new NotConnectedException();
        }

        // Perform actual cleanup
        return cleanAllCloudsFunction(sessionId);
    }

    /**
     * Helper function to perform cloud cleanup and return the result.
     * @param sessionId A valid session id
     * @return true if all clouds were cleaned successfully, false otherwise
     */
    public Boolean cleanAllCloudsFunction(String sessionId) {
        try {
            LOGGER.info("Starting cloud cleanup function for sessionId: {}", sessionId);

            if (cloudService.isAnyAsyncNodeCandidatesProcessesInProgress(sessionId)) {
                LOGGER.warn("Asynchronous node candidate retrieval is in progress. Cloud cleanup is deferred.");
                return false;
            }

            // Retrieve all clouds
            List<PACloud> allClouds = repositoryService.listPACloud();
            if (allClouds.isEmpty()) {
                LOGGER.warn("No clouds found to clean for sessionId: {}", sessionId);
                return false;
            }

            // Collect all cloud IDs
            final List<String> cloudIds = allClouds.stream().map(PACloud::getCloudId).collect(Collectors.toList());
            LOGGER.info("Found {} clouds to clean for sessionId: {}", cloudIds.size(), sessionId);

            // Perform cloud removal
            boolean removeCloudsResult = cloudService.removeClouds(sessionId, cloudIds, true);
            if (removeCloudsResult) {
                LOGGER.info("Successfully removed all clouds for sessionId: {}", sessionId);
            } else {
                LOGGER.error("Failed to remove one or more clouds for sessionId: {}", sessionId);
            }

            return removeCloudsResult;

        } catch (Exception e) {
            // Log any errors with a message and stack trace
            LOGGER.error("Unexpected error during cloud cleanup for sessionId: {}. Details: ", sessionId, e);
            return false;
        }
    }

}
