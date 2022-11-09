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
package org.ow2.proactive.sal.service.service;

import java.util.LinkedList;
import java.util.List;

import org.ow2.proactive.sal.service.model.NodeCandidate;
import org.ow2.proactive.sal.service.model.Requirement;
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
