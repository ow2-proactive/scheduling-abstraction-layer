/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;


/**
 * A node creatable by the system
 */
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
@Getter
@Setter
@Entity
@Table(name = "NODE_CANDIDATE")
public class NodeCandidate implements Serializable {
    public static final String JSON_ID = "id";

    public static final String JSON_NODE_CANDIDATE_TYPE = "nodeCandidateType";

    public static final String JSON_JOB_ID_FOR_BYON = "jobIdForByon";

    public static final String JSON_JOB_ID_FOR_EDGE = "jobIdForEdge";

    public static final String JSON_PRICE = "price";

    public static final String JSON_CLOUD = "cloud";

    public static final String JSON_LOCATION = "location";

    public static final String JSON_IMAGE = "image";

    public static final String JSON_HARDWARE = "hardware";

    public static final String JSON_PRICE_PER_INVOCATION = "pricePerInvocation";

    public static final String JSON_MEMORY_PRICE = "memoryPrice";

    public static final String JSON_NODE_ID = "nodeId";

    public static final String JSON_ENVIRONMENT = "environment";

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID")
    @JsonProperty(JSON_ID)
    private String id = null;

    /**
     * Gets or Sets nodeCandidateType
     */
    public enum NodeCandidateTypeEnum {
        IAAS("IAAS"),
        FAAS("FAAS"),
        PAAS("PAAS"),
        BYON("BYON"),
        EDGE("EDGE"),
        SIMULATION("SIMULATION");

        private String value;

        NodeCandidateTypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static NodeCandidateTypeEnum fromValue(String text) {
            for (NodeCandidateTypeEnum b : NodeCandidateTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text.toUpperCase(Locale.ROOT))) {
                    return b;
                }
            }
            return null;
        }
    }

    @Column(name = "NODE_CANDIDATE_TYPE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_NODE_CANDIDATE_TYPE)
    private NodeCandidateTypeEnum nodeCandidateType = null;

    @Column(name = "JOB_ID_FOR_BYON")
    @JsonProperty(JSON_JOB_ID_FOR_BYON)
    private String jobIdForBYON;

    @Column(name = "JOB_ID_FOR_EDGE")
    @JsonProperty(JSON_JOB_ID_FOR_EDGE)
    private String jobIdForEDGE;

    @Column(name = "PRICE")
    @JsonProperty(JSON_PRICE)
    private Double price = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH })
    @JsonProperty(JSON_CLOUD)
    private Cloud cloud = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH })
    @JsonProperty(JSON_LOCATION)
    private Location location = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH })
    @JsonProperty(JSON_IMAGE)
    private Image image = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH })
    @JsonProperty(JSON_HARDWARE)
    private Hardware hardware = null;

    @Column(name = "PRICE_PER_INVOCATION")
    @JsonProperty(JSON_PRICE_PER_INVOCATION)
    private Double pricePerInvocation = null;

    @Column(name = "MEMORY_PRICE")
    @JsonProperty(JSON_MEMORY_PRICE)
    private Double memoryPrice = null;

    @Column(name = "NODE_ID")
    @JsonProperty(JSON_NODE_ID)
    private String nodeId = null;

    @Embedded
    @JsonProperty(JSON_ENVIRONMENT)
    private Environment environment = null;

    /**
     * Check if a node candidate is of BYON type
     * @return true if yes, false if not
     */
    @JsonIgnore
    public boolean isByonNodeCandidate() {
        return nodeCandidateType.equals(NodeCandidateTypeEnum.BYON);
    }

    /**
     * Check if a node candidate is of EDGE type
     * @return true if yes, false if not
     */
    @JsonIgnore
    public boolean isEdgeNodeCandidate() {
        return nodeCandidateType.equals(NodeCandidateTypeEnum.EDGE);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NodeCandidate {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    nodeCandidateType: ").append(toIndentedString(nodeCandidateType)).append("\n");
        sb.append("    jobIdForBYON: ").append(toIndentedString(jobIdForBYON)).append("\n");
        sb.append("    jobIdForEDGE: ").append(toIndentedString(jobIdForEDGE)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
        sb.append("    cloud: ").append(toIndentedString(cloud)).append("\n");
        sb.append("    image: ").append(toIndentedString(image)).append("\n");
        sb.append("    hardware: ").append(toIndentedString(hardware)).append("\n");
        sb.append("    location: ").append(toIndentedString(location)).append("\n");
        sb.append("    pricePerInvocation: ").append(toIndentedString(pricePerInvocation)).append("\n");
        sb.append("    memoryPrice: ").append(toIndentedString(memoryPrice)).append("\n");
        sb.append("    nodeId: ").append(toIndentedString(nodeId)).append("\n");
        sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
