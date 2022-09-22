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
package org.ow2.proactive.sal.service.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.ow2.proactive.sal.service.util.EntityManagerHelper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "TASK")
public class Task implements Serializable {
    @Id
    @Column(name = "TASK_ID")
    private String taskId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TYPE")
    private String type;

    @Embedded
    private CommandsInstallation installation;

    @Embedded
    private DockerEnvironment environment;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Deployment> deployments;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REFRESH)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Port> portsToOpen;

    @Column(name = "PARENT_TASKS")
    @ElementCollection(targetClass = String.class)
    private List<String> parentTasks;

    @Column(name = "SUBMITTED_TASK_NAMES")
    @ElementCollection(targetClass = String.class)
    private List<String> submittedTaskNames;

    @Column(name = "DEPLOYMENT_FIRST_SUBMITTED_TASK_NAME")
    private String deploymentFirstSubmittedTaskName;

    @Column(name = "DEPLOYMENT_LAST_SUBMITTED_TASK_NAME")
    private String deploymentLastSubmittedTaskName;

    @Column(name = "NEXT_DEPLOYMENT_ID")
    private Long nextDeploymentID = 0L;

    public static void clean() {
        List<Task> allTasks = EntityManagerHelper.createQuery("SELECT t FROM Task t", Task.class).getResultList();
        allTasks.forEach(EntityManagerHelper::remove);
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
}
