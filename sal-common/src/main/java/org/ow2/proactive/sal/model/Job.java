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
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "JOB")
public class Job implements Serializable {
    @Id
    @Column(name = "JOB_ID")
    private String jobId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VARIABLES")
    @ElementCollection(targetClass = String.class)
    private Map<String, String> variables;

    @Column(name = "SUBMITTED_JOB_ID")
    private long submittedJobId = 0L;

    @Column(name = "SUBMITTED_JOB_TYPE")
    @Enumerated(EnumType.STRING)
    private SubmittedJobType submittedJobType;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.REFRESH)
    private List<Task> tasks;

    public Task findTask(String taskName) {
        return tasks.stream().filter(task -> task.getName().equals(taskName)).findAny().orElse(null);
    }
}
