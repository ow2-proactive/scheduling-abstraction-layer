/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REFRESH)
    private List<Task> tasks;

    @ElementCollection
    @CollectionTable(name = "JOB_COMMUNICATIONS_MAPPING", joinColumns = { @JoinColumn(name = "JOB_ID", referencedColumnName = "JOB_ID") })
    @MapKeyColumn(name = "PROVIDED_PORT_NAME")
    @Column(name = "REQUIRED_PORT_NAME")
    private Map<String, String> communications;

    public Task findTask(String taskName) {
        return tasks.stream().filter(task -> task.getName().equals(taskName)).findAny().orElse(null);
    }

    public Set<Task> getRootTasks() {
        return this.getTasks()
                   .stream()
                   .filter(task -> task.getParentTasks() == null || task.getParentTasks().isEmpty())
                   .collect(Collectors.toSet());
    }

    public Set<Task> getSinkTasks() {
        return this.getTasks()
                   .stream()
                   .filter(task -> this.getTasks()
                                       .stream()
                                       .noneMatch(task1 -> task1.getParentTasks() != null &&
                                                           task1.getParentTasks().containsValue(task.getName())))
                   .collect(Collectors.toSet());
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
    }

    public void removeTask(Task taskToDelete) {
        tasks.remove(taskToDelete);
    }

    @PreRemove
    private void cleanMappedDataFirst() {
        this.communications.clear();
        this.variables.clear();
    }
}
