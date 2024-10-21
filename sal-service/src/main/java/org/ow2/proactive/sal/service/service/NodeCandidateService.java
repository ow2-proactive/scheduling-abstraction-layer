/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.util.LinkedList;
import java.util.List;

import org.ow2.proactive.sal.model.NodeCandidate;
import org.ow2.proactive.sal.model.Requirement;
import org.ow2.proactive.sal.service.nc.NodeCandidateUtils;
import org.ow2.proactive.sal.service.nc.WhiteListedInstanceTypesUtils;
import org.ow2.proactive.sal.service.util.JCloudsInstancesUtils;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service("NodeCandidateService")
public class NodeCandidateService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * Find node candidates
     * @param sessionId A valid session id
     * @param requirements List of NodeType or Attribute requirements
     * @return A list of all node candidates that satisfy the requirements
     */
    public List<NodeCandidate> findNodeCandidates(String sessionId, List<Requirement> requirements)
            throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<NodeCandidate> filteredNodeCandidates = new LinkedList<>();
        List<NodeCandidate> allNodeCandidates = repositoryService.listNodeCandidates();
        allNodeCandidates.forEach(nodeCandidate -> {
            LOGGER.info("Checking node candidate with type: {}", nodeCandidate.getHardware().getName());
            if (nodeCandidate.isByonNodeCandidate() || nodeCandidate.isEdgeNodeCandidate() ||
                JCloudsInstancesUtils.isHandledHardwareInstanceType(nodeCandidate.getCloud().getApi().getProviderName(),
                                                                    nodeCandidate.getHardware().getName()) ||
                WhiteListedInstanceTypesUtils.isHandledHardwareInstanceType(nodeCandidate.getHardware().getName())) {
                LOGGER.info("  Checking filters for node candidate with type: {} ...",
                            nodeCandidate.getHardware().getName());
                if (NodeCandidateUtils.verifyAllFilters(requirements, nodeCandidate)) {
                    LOGGER.info("   Requirements answered. Success!");
                    filteredNodeCandidates.add(nodeCandidate);
                }
            }
        });
        return filteredNodeCandidates;
        //TODO: add BYON nodes to the NodeCandidates List
    }

    /**
     * This function returns the number of available node candidates according to the added clouds
     * @param sessionId A valid session id
     * @return the number of available node candidates
     */
    public Long getLengthOfNodeCandidates(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        List<NodeCandidate> allNodeCandidates = repositoryService.listNodeCandidates();
        return (long) allNodeCandidates.size();
    }
}
