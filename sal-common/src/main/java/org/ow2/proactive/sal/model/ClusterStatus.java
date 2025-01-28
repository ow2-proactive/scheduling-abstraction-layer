/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * @author ActiveEon Team
 * @since 1/24/2025
 */
public enum ClusterStatus {

    //this status is assinged to cluster after calling Define Cluster endpoint
    DEFINED("Defined"),
    //this status is assigned when all the nodes in cluster successfully finished their deployment (having corresponding JobStatus FINISHED for all nodes)
    DEPLOYED("Deployed"),
    //this status is assigned to cluster when one or more nodes were not successfuly deployed (having corresponding  JobStatus FAILED, KILLED, IN_ERROR or CANCELED)
    FAILED("Failed"),
    //this status is assigned to the cluster when it is sent for the deployment by using Deploy Cluster endpoint
    SUBMITTED("Submitted"),
    //this status is assigned to the cluster when ScaleIn or ScaleOut operations are called.
    SCALING("Scaling"),

    OTHER("other"); // For any unknown status

    private final String value;

    ClusterStatus(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @JsonCreator
    public static ClusterStatus fromValue(String text) {
        for (ClusterStatus type : ClusterStatus.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return OTHER; // Return a default type if the input is invalid
    }

}
