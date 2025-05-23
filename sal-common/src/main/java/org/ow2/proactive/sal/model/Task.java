/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;


@Log4j2
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "TASK")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = Deployment.JSON_TASK_ID, scope = Task.class)
public class Task implements Serializable {
    @Id
    @Column(name = "TASK_ID")
    private String taskId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private Installation.InstallationType type;

    @Column(name = "SECURITY_GROUP")
    private String securityGroup = null;

    // Are these two attributes able to be merged into one common abstract?
    @Embedded
    private CommandsInstallation installation;

    @Embedded
    private DockerEnvironment environment;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.MERGE, CascadeType.REFRESH,
                                                                         CascadeType.REMOVE })
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("deploymentNodeNames")
    private List<Deployment> deployments;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE })
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Port> portsToOpen = new LinkedList<>();

    @ElementCollection
    @CollectionTable(name = "TASK_PARENT_TASK_PORT_MAPPING", joinColumns = { @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID") })
    @MapKeyColumn(name = "REQUIRED_PORT_NAME")
    @Column(name = "PARENT_TASK_NAME")
    private Map<String, String> parentTasks;

    @Column(name = "SUBMITTED_TASK_NAMES")
    @ElementCollection(targetClass = String.class)
    private List<String> submittedTaskNames;

    @Column(name = "DEPLOYMENT_FIRST_SUBMITTED_TASK_NAME")
    private String deploymentFirstSubmittedTaskName;

    @Column(name = "DEPLOYMENT_LAST_SUBMITTED_TASK_NAME")
    private String deploymentLastSubmittedTaskName;

    @Column(name = "NEXT_DEPLOYMENT_ID")
    private Long nextDeploymentID = 0L;

    //    This is added for deserialization testing purpose
    public Task(String taskId) {
        this.taskId = taskId;
    }

    //    This is added for deserialization testing purpose
    @JsonSetter("deploymentNodeNames")
    private void setDeploymentsByIds(List<String> deployments) {
        this.deployments = deployments.stream().map(Deployment::new).collect(Collectors.toList());
        this.deployments.forEach(deployment -> deployment.setTask(this));
    }

    public void addDeployment(Deployment deployment) {
        if (deployments == null) {
            deployments = new LinkedList<>();
        }
        deployments.add(deployment);
        nextDeploymentID++;
    }

    public void removeDeployment(Deployment deployment) {
        deployments.remove(deployment);
    }

    public void addSubmittedTaskName(String submittedTaskName) {
        if (submittedTaskNames == null) {
            submittedTaskNames = new LinkedList<>();
        }
        submittedTaskNames.add(submittedTaskName);
    }

    public void setInstallationByType(Installation installation) {
        if (installation instanceof DockerEnvironment) {
            this.setEnvironment((DockerEnvironment) installation);
            LOGGER.info("vars calculated" + ((DockerEnvironment) installation).getEnvironmentVars());
        } else if (installation instanceof CommandsInstallation) {
            this.setInstallation((CommandsInstallation) installation);
        } else {
            throw new IllegalArgumentException("Task type not supported yet.");
        }
    }

    public String serializePortsToOpenToVariableMap() {
        if (this.getPortsToOpen().isEmpty())
            return "[]";
        StringBuilder portsJson = new StringBuilder("[" + this.getPortsToOpen().get(0).serializeToVariableMap());
        for (int i = 1; i < this.getPortsToOpen().size(); i++) {
            portsJson.append(",").append(this.getPortsToOpen().get(i).serializeToVariableMap());
        }
        portsJson.append("]");
        return portsJson.toString();
    }

    @PreRemove
    private void cleanMappedDataFirst() {
        this.parentTasks.clear();
        this.submittedTaskNames.clear();
    }
}
