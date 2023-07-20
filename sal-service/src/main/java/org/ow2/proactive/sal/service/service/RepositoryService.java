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

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

import java.util.List;
import java.util.Optional;

import org.ow2.proactive.sal.model.*;
import org.ow2.proactive.sal.service.repository.*;
import org.ow2.proactive.scheduler.common.exception.NotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service
@Transactional(isolation = READ_COMMITTED, propagation = Propagation.REQUIRED)
public class RepositoryService {

    @Autowired
    private PAGatewayService paGatewayService;

    @Autowired
    private ByonNodeRepository byonNodeRepository;

    @Autowired
    private CloudRepository cloudRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private DeploymentRepository deploymentRepository;

    @Autowired
    private EdgeNodeRepository edgeNodeRepository;

    @Autowired
    private EmsDeploymentRequestRepository emsDeploymentRequestRepository;

    @Autowired
    private HardwareRepository hardwareRepository;

    @Autowired
    private IaasNodeRepository iaasNodeRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private NodeCandidateRepository nodeCandidateRepository;

    @Autowired
    private PACloudRepository paCloudRepository;

    @Autowired
    private PortRepository portRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private VaultKeyRepository vaultKeyRepository;

    private static final String DATABASE_LOGS_SIGNATURE = "from the database ...";

    /**
     * Find the byonNode that match with the byonNodeId
     * @param byonNodeId the id of the instance
     * @return the byonNode that match with the byonNodeId
     */
    public ByonNode getByonNode(String byonNodeId) {
        return byonNodeRepository.findOne(byonNodeId);
    }

    /**
     * List all ByonNode entries
     */
    public List<ByonNode> listByonNodes() {
        return byonNodeRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param byonNode is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized ByonNode saveByonNode(ByonNode byonNode) {
        return byonNodeRepository.saveAndFlush(byonNode);
    }

    /**
     * Delete the byonNode that match with the byonNodeId
     * @param byonNodeId the id of the instance
     * @return the deleted byonNode
     */
    @Modifying(clearAutomatically = true)
    public ByonNode deleteByonNode(String byonNodeId) {
        return deleteByonNode(getByonNode(byonNodeId));
    }

    /**
     * Deleting the BYON node and its Node candidate from the database
     * @param byonNode an object of class ByonNode to be removed.
     * @return the deleted byonNode
     */
    @Modifying(clearAutomatically = true)
    public ByonNode deleteByonNode(ByonNode byonNode) {
        LOGGER.info("Removing the BYON node {} {}", byonNode.getId(), DATABASE_LOGS_SIGNATURE);
        NodeCandidate byonNC = byonNode.getNodeCandidate();
        Location byonLocation = byonNC.getLocation();
        Hardware byonHardware = byonNC.getHardware();
        Image byonImage = byonNC.getImage();
        LOGGER.info("Removing the BYON Location {} {}", byonLocation.getId(), DATABASE_LOGS_SIGNATURE);
        locationRepository.delete(byonLocation);
        LOGGER.info("Removing the BYON Hardware {} {}", byonHardware.getId(), DATABASE_LOGS_SIGNATURE);
        hardwareRepository.delete(byonHardware);
        LOGGER.info("Removing the BYON Image {} {}", byonImage.getId(), DATABASE_LOGS_SIGNATURE);
        imageRepository.delete(byonImage);
        LOGGER.info("Removing the BYON Node Candidate {} {}", byonNC.getId(), DATABASE_LOGS_SIGNATURE);
        nodeCandidateRepository.delete(byonNC);
        byonNodeRepository.delete(byonNode);
        byonNodeRepository.flush();
        LOGGER.info("BYON node {} removed.", byonNode.getId());
        return byonNode;
    }

    /**
     * Find the cloud that match with the cloudId
     * @param cloudId the id of the instance
     * @return the cloud that match with the cloudId
     */
    public Cloud getCloud(String cloudId) {
        return cloudRepository.findOne(cloudId);
    }

    /**
     * List all Cloud entries
     */
    public List<Cloud> listClouds() {
        return cloudRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param cloud is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Cloud saveCloud(Cloud cloud) {
        return cloudRepository.saveAndFlush(cloud);
    }

    /**
     * Delete the cloud that match with the cloudId
     * @param cloudId the id of the instance
     * @return the deleted cloud
     */
    @Modifying(clearAutomatically = true)
    public Cloud deleteCloud(String cloudId) {
        Cloud instanceToRemove = getCloud(cloudId);
        //TODO To Complete removing
        cloudRepository.delete(cloudId);
        return instanceToRemove;
    }

    /**
     * Find the credentials that match with the credentialsId
     * @param credentialsId the id of the instance
     * @return the credentials that match with the credentialsId
     */
    public Credentials getCredentials(Integer credentialsId) {
        return credentialsRepository.findOne(credentialsId);
    }

    /**
     * List all Credentials entries
     */
    public List<Credentials> listCredentials() {
        return credentialsRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param credentials is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Credentials saveCredentials(Credentials credentials) {
        return credentialsRepository.saveAndFlush(credentials);
    }

    /**
     * Delete the credentials that match with the credentialsId
     * @param credentialsId the id of the instance
     * @return the deleted credentials
     */
    @Modifying(clearAutomatically = true)
    public Credentials deleteCredentials(Integer credentialsId) {
        Credentials instanceToRemove = getCredentials(credentialsId);
        //TODO To Complete removing
        credentialsRepository.delete(credentialsId);
        return instanceToRemove;
    }

    /**
     * Find the deployment that match with the deploymentId
     * @param deploymentId the id of the instance
     * @return the deployment that match with the deploymentId
     */
    public Deployment getDeployment(String deploymentId) {
        return deploymentRepository.findOne(deploymentId);
    }

    /**
     * Find deployments that match with the node names list
     * @param nodeNames valid deployment node names
     * @return deployments that match with the node names list
     */
    public List<Deployment> findAllDeployments(List<String> nodeNames) {
        return deploymentRepository.findAll(nodeNames);
    }

    /**
     * List all Deployment entries
     */
    public List<Deployment> listDeployments() {
        return deploymentRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param deployment is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Deployment saveDeployment(Deployment deployment) {
        return deploymentRepository.saveAndFlush(deployment);
    }

    /**
     * Delete the deployment that match with the deploymentId
     * @param deploymentId the id of the instance
     * @return the deleted deployment
     */
    @Modifying(clearAutomatically = true)
    public Deployment deleteDeployment(String deploymentId) {
        Deployment instanceToRemove = getDeployment(deploymentId);
        return deleteDeployment(instanceToRemove);
    }

    /**
     * Delete the deployment entry
     * @param deployment the id of the instance
     * @return the deleted deployment
     */
    @Modifying(clearAutomatically = true)
    public Deployment deleteDeployment(Deployment deployment) {
        // remove deployments from paCloud
        LOGGER.info("Cleaning deployment \"{}\" from the cloud entry and related tasks", deployment.getNodeName());
        deployment.getPaCloud().removeDeployment(deployment);
        savePACloud(deployment.getPaCloud());
        // decrease iaasnode
        if (NodeType.IAAS.equals(deployment.getDeploymentType()) && Boolean.TRUE.equals(deployment.getIsDeployed())) {
            deployment.getIaasNode().decDeployedNodes(1L);
            saveIaasNode(deployment.getIaasNode());
        }
        // ?check byonnode/edge?
        // remove deployment from task
        deployment.getTask().removeDeployment(deployment);
        saveTask(deployment.getTask());
        // remove deployment
        deploymentRepository.delete(deployment);
        return deployment;
    }

    /**
     * Find the edgeNode that match with the edgeNodeId
     * @param edgeNodeId the id of the instance
     * @return the edgeNode that match with the edgeNodeId
     */
    public EdgeNode getEdgeNode(String edgeNodeId) {
        return edgeNodeRepository.findOne(edgeNodeId);
    }

    /**
     * List all EdgeNode entries
     */
    public List<EdgeNode> listEdgeNodes() {
        return edgeNodeRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param edgeNode is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized EdgeNode saveEdgeNode(EdgeNode edgeNode) {
        return edgeNodeRepository.saveAndFlush(edgeNode);
    }

    /**
     * Delete the edgeNode that match with the edgeNodeId
     * @param edgeNodeId the id of the instance
     * @return the deleted edgeNode
     */
    @Modifying(clearAutomatically = true)
    public EdgeNode deleteEdgeNode(String edgeNodeId) {
        return deleteEdgeNode(getEdgeNode(edgeNodeId));
    }

    /**
     * Deleting the Edge node and its Node candidate from the database
     * @param edgeNode an object of class EdgeNode to be removed.
     * @return the deleted EdgeNode
     */
    @Modifying(clearAutomatically = true)
    public EdgeNode deleteEdgeNode(EdgeNode edgeNode) {
        LOGGER.info("Removing the EDGE node {} {}", edgeNode.getId(), DATABASE_LOGS_SIGNATURE);
        NodeCandidate edgeNC = edgeNode.getNodeCandidate();
        Location edgeLocation = edgeNC.getLocation();
        Hardware edgeHardware = edgeNC.getHardware();
        Image edgeImage = edgeNC.getImage();
        LOGGER.info("Removing the EDGE Location {} {}", edgeLocation.getId(), DATABASE_LOGS_SIGNATURE);
        locationRepository.delete(edgeLocation);
        LOGGER.info("Removing the EDGE Hardware {} {}", edgeHardware.getId(), DATABASE_LOGS_SIGNATURE);
        hardwareRepository.delete(edgeHardware);
        LOGGER.info("Removing the EDGE Image {} {}", edgeImage.getId(), DATABASE_LOGS_SIGNATURE);
        imageRepository.delete(edgeImage);
        LOGGER.info("Removing the EDGE Node Candidate {} {}", edgeNC.getId(), DATABASE_LOGS_SIGNATURE);
        nodeCandidateRepository.delete(edgeNC);
        edgeNodeRepository.delete(edgeNode);
        edgeNodeRepository.flush();
        LOGGER.info("EDGE node {} removed.", edgeNode.getId());
        return edgeNode;
    }

    /**
     * Find the emsDeploymentRequest that match with the emsDeploymentRequestId
     * @param emsDeploymentRequestId the id of the instance
     * @return the emsDeploymentRequest that match with the emsDeploymentRequestId
     */
    public EmsDeploymentRequest getEmsDeploymentRequest(String emsDeploymentRequestId) {
        return emsDeploymentRequestRepository.findOne(emsDeploymentRequestId);
    }

    /**
     * List all EmsDeploymentRequest entries
     */
    public List<EmsDeploymentRequest> listEmsDeploymentRequests() {
        return emsDeploymentRequestRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param emsDeploymentRequest is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized EmsDeploymentRequest saveEmsDeploymentRequest(EmsDeploymentRequest emsDeploymentRequest) {
        return emsDeploymentRequestRepository.saveAndFlush(emsDeploymentRequest);
    }

    /**
     * Delete the emsDeploymentRequest that match with the emsDeploymentRequestId
     * @param emsDeploymentRequestId the id of the instance
     * @return the deleted emsDeploymentRequest
     */
    @Modifying(clearAutomatically = true)
    public EmsDeploymentRequest deleteEmsDeploymentRequest(String emsDeploymentRequestId) {
        EmsDeploymentRequest instanceToRemove = getEmsDeploymentRequest(emsDeploymentRequestId);
        //TODO To Complete removing
        emsDeploymentRequestRepository.delete(emsDeploymentRequestId);
        return instanceToRemove;
    }

    /**
     * Find the hardware that match with the hardwareId
     * @param hardwareId the id of the instance
     * @return the hardware that match with the hardwareId
     */
    public Hardware getHardware(String hardwareId) {
        return hardwareRepository.findOne(hardwareId);
    }

    /**
     * List all Hardware entries
     */
    public List<Hardware> listHardwares() {
        return hardwareRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param hardware is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Hardware saveHardware(Hardware hardware) {
        return hardwareRepository.saveAndFlush(hardware);
    }

    /**
     * Delete the hardware that match with the hardwareId
     * @param hardwareId the id of the instance
     * @return the deleted hardware
     */
    @Modifying(clearAutomatically = true)
    public Hardware deleteHardware(String hardwareId) {
        Hardware instanceToRemove = getHardware(hardwareId);
        //TODO To Complete removing
        hardwareRepository.delete(hardwareId);
        return instanceToRemove;
    }

    /**
     * Find the iaasNode that match with the iaasNodeId
     * @param iaasNodeId the id of the instance
     * @return the iaasNode that match with the iaasNodeId
     */
    public IaasNode getIaasNode(String iaasNodeId) {
        return iaasNodeRepository.findOne(iaasNodeId);
    }

    /**
     * List all IaasNode entries
     */
    public List<IaasNode> listIaasNodes() {
        return iaasNodeRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param iaasNode is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized IaasNode saveIaasNode(IaasNode iaasNode) {
        return iaasNodeRepository.saveAndFlush(iaasNode);
    }

    /**
     * Delete the iaasNode that match with the iaasNodeId
     * @param iaasNodeId the id of the instance
     * @return the deleted iaasNode
     */
    @Modifying(clearAutomatically = true)
    public IaasNode deleteIaasNode(String iaasNodeId) {
        IaasNode instanceToRemove = getIaasNode(iaasNodeId);
        //TODO To Complete removing
        iaasNodeRepository.delete(iaasNodeId);
        return instanceToRemove;
    }

    /**
     * Add or update the instance data given in param
     * @param node is the instance data to add or update, its instance id will be use as a key
     */
    public Node saveNode(Node node) {
        if (node instanceof EdgeNode) {
            this.saveEdgeNode((EdgeNode) node);
        } else if (node instanceof ByonNode) {
            this.saveByonNode((ByonNode) node);
        } else {
            this.saveIaasNode((IaasNode) node);
        }
        return node;
    }

    /**
     * Find the image that match with the imageId
     * @param imageId the id of the instance
     * @return the image that match with the imageId
     */
    public Image getImage(String imageId) {
        return imageRepository.findOne(imageId);
    }

    /**
     * List all Image entries
     */
    public List<Image> listImages() {
        return imageRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param image is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Image saveImage(Image image) {
        return imageRepository.saveAndFlush(image);
    }

    /**
     * Delete the image that match with the imageId
     * @param imageId the id of the instance
     * @return the deleted image
     */
    @Modifying(clearAutomatically = true)
    public Image deleteImage(String imageId) {
        Image instanceToRemove = getImage(imageId);
        //TODO To Complete removing
        imageRepository.delete(imageId);
        return instanceToRemove;
    }

    /**
     * Find the job that match with the jobId
     * @param jobId the id of the instance
     * @return the job that match with the jobId
     */
    public Job getJob(String jobId) {
        return jobRepository.findOne(jobId);
    }

    /**
     * List all Job entries
     */
    public List<Job> listJobs() {
        return jobRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param job is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Job saveJob(Job job) {
        return jobRepository.saveAndFlush(job);
    }

    /**
     * Delete the job that match with the jobId
     * @param jobId the id of the instance
     * @return the deleted job
     */
    @Modifying(clearAutomatically = true)
    public Job deleteJob(String jobId) {
        Job instanceToRemove = getJob(jobId);
        //TODO To Complete removing
        jobRepository.delete(jobId);
        return instanceToRemove;
    }

    /**
     * Find the location that match with the locationId
     * @param locationId the id of the instance
     * @return the location that match with the locationId
     */
    public Location getLocation(String locationId) {
        return locationRepository.findOne(locationId);
    }

    /**
     * List all Location entries
     */
    public List<Location> listLocations() {
        return locationRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param location is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Location saveLocation(Location location) {
        return locationRepository.saveAndFlush(location);
    }

    /**
     * Delete the location that match with the locationId
     * @param locationId the id of the instance
     * @return the deleted location
     */
    @Modifying(clearAutomatically = true)
    public Location deleteLocation(String locationId) {
        Location instanceToRemove = getLocation(locationId);
        //TODO To Complete removing
        locationRepository.delete(locationId);
        return instanceToRemove;
    }

    /**
     * Find the nodeCandidate that match with the nodeCandidateId
     * @param nodeCandidateId the id of the instance
     * @return the nodeCandidate that match with the nodeCandidateId
     */
    public NodeCandidate getNodeCandidate(String nodeCandidateId) {
        return nodeCandidateRepository.findOne(nodeCandidateId);
    }

    /**
     * List all NodeCandidate entries
     */
    public List<NodeCandidate> listNodeCandidates() {
        return nodeCandidateRepository.findAll();
    }

    /**
     * Find the first node candidate that match a given hardware
     * @param hardware A given stored hardware
     * @return The first found node candidate that uses the given hardware
     */
    public NodeCandidate findFirstNodeCandidateWithHardware(Hardware hardware) {
        List<NodeCandidate> nodeCandidates = this.listNodeCandidates();
        Optional<NodeCandidate> optNodeCandidate = nodeCandidates.stream()
                                                                 .filter(nodeCandidate -> hardware.equals(nodeCandidate.getHardware()))
                                                                 .findFirst();
        return optNodeCandidate.orElse(null);
    }

    /**
     * Add or update the instance data given in param
     * @param nodeCandidate is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized NodeCandidate saveNodeCandidate(NodeCandidate nodeCandidate) {
        return nodeCandidateRepository.saveAndFlush(nodeCandidate);
    }

    /**
     * Delete the nodeCandidate that match with the nodeCandidateId
     * @param nodeCandidateId the id of the instance
     * @return the deleted nodeCandidate
     */
    @Modifying(clearAutomatically = true)
    public NodeCandidate deleteNodeCandidate(String nodeCandidateId) {
        NodeCandidate instanceToRemove = getNodeCandidate(nodeCandidateId);
        this.deleteOrphanNode(instanceToRemove);
        nodeCandidateRepository.delete(nodeCandidateId);
        cloudRepository.getOrphanCloudIds().forEach(this::deleteCloud);
        imageRepository.getOrphanImageIds().forEach(this::deleteImage);
        hardwareRepository.getOrphanHardwareIds().forEach(this::deleteHardware);
        locationRepository.getOrphanLocationIds().forEach(this::deleteLocation);
        return instanceToRemove;
    }

    /**
     * Delete the nodeCandidate to be removed related node
     * @param nodeCandidateToBeRemoved the node candidate to be removed
     */
    @Modifying(clearAutomatically = true)
    private void deleteOrphanNode(NodeCandidate nodeCandidateToBeRemoved) {
        switch (nodeCandidateToBeRemoved.getNodeCandidateType()) {
            case IAAS:
                iaasNodeRepository.delete(nodeCandidateToBeRemoved.getNodeId());
                break;
            case BYON:
                byonNodeRepository.delete(nodeCandidateToBeRemoved.getNodeId());
                break;
            case EDGE:
                edgeNodeRepository.delete(nodeCandidateToBeRemoved.getNodeId());
                break;
            default:
                LOGGER.warn("To be deleted node type not supported yet!");
        }
    }

    /**
     * Find the paCloud that match with the paCloudId
     * @param paCloudId the id of the instance
     * @return the paCloud that match with the paCloudId
     */
    public PACloud getPACloud(String paCloudId) {
        return paCloudRepository.findOne(paCloudId);
    }

    /**
     * Find clouds that match with the cloud ids list
     * @param cloudIds valid cloud ids
     * @return clouds that match with the cloud ids list
     */
    public List<PACloud> findAllPAClouds(List<String> cloudIds) {
        return paCloudRepository.findAll(cloudIds);
    }

    /**
     * List all PACloud entries
     */
    public List<PACloud> listPACloud() {
        return paCloudRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param paCloud is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized PACloud savePACloud(PACloud paCloud) {
        return paCloudRepository.saveAndFlush(paCloud);
    }

    /**
     * Delete the paCloud that match with the paCloudId
     * @param paCloudId the id of the instance
     * @return the deleted paCloud
     */
    @Modifying(clearAutomatically = true)
    public PACloud deletePACloud(String paCloudId) {
        PACloud instanceToRemove = getPACloud(paCloudId);
        return deletePACloud(instanceToRemove);
    }

    /**
     * Delete an paCloud instance
     * @param cloud the instance to remove
     * @return the deleted paCloud
     */
    @Modifying(clearAutomatically = true)
    public PACloud deletePACloud(PACloud cloud) {
        paCloudRepository.delete(cloud);
        return cloud;
    }

    /**
     * Find the port that match with the portId
     * @param portId the id of the instance
     * @return the port that match with the portId
     */
    public Port getPort(Integer portId) {
        return portRepository.findOne(portId);
    }

    /**
     * List all Port entries
     */
    public List<Port> listPorts() {
        return portRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param port is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Port savePort(Port port) {
        return portRepository.saveAndFlush(port);
    }

    /**
     * Delete the port that match with the portId
     * @param portId the id of the instance
     * @return the deleted port
     */
    @Modifying(clearAutomatically = true)
    public Port deletePort(Integer portId) {
        Port instanceToRemove = getPort(portId);
        //TODO To Complete removing
        portRepository.delete(portId);
        return instanceToRemove;
    }

    /**
     * Find the task that match with the taskId
     * @param taskId the id of the instance
     * @return the task that match with the taskId
     */
    public Task getTask(String taskId) {
        return taskRepository.findOne(taskId);
    }

    /**
     * List all Task entries
     */
    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param task is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized Task saveTask(Task task) {
        return taskRepository.saveAndFlush(task);
    }

    /**
     * Delete the task that match with the taskId
     * @param taskId the id of the instance
     * @return the deleted task
     */
    @Modifying(clearAutomatically = true)
    public Task deleteTask(String taskId) {
        return deleteTask(getTask(taskId));
    }

    /**
     * Delete the task
     * @param taskToRemove the task to remove
     * @return the deleted task
     */
    @Modifying(clearAutomatically = true)
    public Task deleteTask(Task taskToRemove) {
        while (!taskToRemove.getDeployments().isEmpty())
            deleteDeployment(taskToRemove.getDeployments().get(0));
        taskRepository.delete(taskToRemove);
        return taskToRemove;
    }

    /**
     * Find the vaultKey that match with the vaultKeyId
     * @param vaultKeyId the id of the instance
     * @return the vaultKey that match with the vaultKeyId
     */
    public VaultKey getVaultKey(String vaultKeyId) {
        return vaultKeyRepository.findOne(vaultKeyId);
    }

    /**
     * List all VaultKey entries
     */
    public List<VaultKey> listVaultKeys() {
        return vaultKeyRepository.findAll();
    }

    /**
     * Add or update the instance data given in param
     * @param vaultKey is the instance data to add or update, its instance id will be use as a key
     */
    public synchronized VaultKey saveVaultKey(VaultKey vaultKey) {
        return vaultKeyRepository.saveAndFlush(vaultKey);
    }

    /**
     * Delete the vaultKey that match with the vaultKeyId
     * @param vaultKeyId the id of the instance
     * @return the deleted vaultKey
     */
    @Modifying(clearAutomatically = true)
    public VaultKey deleteVaultKey(String vaultKeyId) {
        VaultKey instanceToRemove = getVaultKey(vaultKeyId);
        vaultKeyRepository.delete(vaultKeyId);
        return instanceToRemove;
    }

    /**
     * Flush all DB entries
     */
    public synchronized void flush() {
        jobRepository.flush();
        taskRepository.flush();
        paCloudRepository.flush();
        emsDeploymentRequestRepository.flush();
        deploymentRepository.flush();
        portRepository.flush();
        credentialsRepository.flush();
        nodeCandidateRepository.flush();
        cloudRepository.flush();
        imageRepository.flush();
        hardwareRepository.flush();
        locationRepository.flush();
        iaasNodeRepository.flush();
        byonNodeRepository.flush();
        edgeNodeRepository.flush();
        vaultKeyRepository.flush();
    }

    /**
     * Clean all DB entries
     * @param sessionId A valid session id
     */
    @Modifying(clearAutomatically = true)
    public void cleanAll(String sessionId) throws NotConnectedException {
        if (!paGatewayService.isConnectionActive(sessionId)) {
            throw new NotConnectedException();
        }
        LOGGER.info("Cleaning Jobs ...");
        jobRepository.deleteAll();
        LOGGER.info("Cleaning Deployments ...");
        deploymentRepository.deleteAll();
        LOGGER.info("Cleaning PAClouds ...");
        paCloudRepository.deleteAll();
        LOGGER.info("Cleaning Tasks ...");
        taskRepository.deleteAll();
        LOGGER.info("Cleaning Ports ...");
        portRepository.deleteAll();
        LOGGER.info("Cleaning Credentials ...");
        credentialsRepository.deleteAll();
        LOGGER.info("Cleaning EmsDeploymentRequests ...");
        emsDeploymentRequestRepository.deleteAll();
        LOGGER.info("Cleaning Clouds ...");
        cloudRepository.deleteAll();
        LOGGER.info("Cleaning Images ...");
        imageRepository.deleteAll();
        LOGGER.info("Cleaning Hardwares ...");
        hardwareRepository.deleteAll();
        LOGGER.info("Cleaning IaasNodes ...");
        iaasNodeRepository.deleteAll();
        LOGGER.info("Cleaning ByonNodes ...");
        byonNodeRepository.deleteAll();
        LOGGER.info("Cleaning EdgeNodes ...");
        edgeNodeRepository.deleteAll();
        LOGGER.info("Cleaning NodeCandidates ...");
        nodeCandidateRepository.deleteAll();
        LOGGER.info("Cleaning Locations ...");
        locationRepository.deleteAll();
        LOGGER.info("Cleaning Vault Keys ...");
        vaultKeyRepository.deleteAll();
        LOGGER.info("Done.");
    }
}
