/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractNode implements Node {

    // JSON field constants
    public static final String JSON_ID = "id";
    public static final String JSON_NODE_CANDIDATE = "nodeCandidate";

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID")
    @JsonProperty(JSON_ID)
    protected String id = null;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JsonProperty(JSON_NODE_CANDIDATE)
    protected NodeCandidate nodeCandidate = null;
}
