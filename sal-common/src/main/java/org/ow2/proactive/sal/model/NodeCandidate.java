/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;


/**
 * A node creatable by the system
 */
@Log4j2
@AllArgsConstructor
@NoArgsConstructor
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

    public NodeCandidate id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodeCandidate nodeCandidateType(NodeCandidateTypeEnum nodeCandidateType) {
        this.nodeCandidateType = nodeCandidateType;
        return this;
    }

    /**
     * Get nodeCandidateType
     * @return nodeCandidateType
     **/
    public NodeCandidateTypeEnum getNodeCandidateType() {
        return nodeCandidateType;
    }

    public void setNodeCandidateType(NodeCandidateTypeEnum nodeCandidateType) {
        this.nodeCandidateType = nodeCandidateType;
    }

    /**
     * Get jobIdForBYON
     * @return jobIdForBYON
     **/
    public String getJobIdForBYON() {
        return jobIdForBYON;
    }

    public String getJobIdForEDGE() {
        return jobIdForEDGE;
    }

    public void setJobIdForBYON(String jobIdForBYON) {
        this.jobIdForBYON = jobIdForBYON;
    }

    public void setJobIdForEDGE(String jobIdForEDGE) {
        this.jobIdForEDGE = jobIdForEDGE;
    }

    public NodeCandidate price(Double price) {
        this.price = price;
        return this;
    }

    /**
     * Get price
     * @return price
     **/
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public NodeCandidate cloud(Cloud cloud) {
        this.cloud = cloud;
        return this;
    }

    /**
     * Get cloud
     * @return cloud
     **/
    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public NodeCandidate image(Image image) {
        this.image = image;
        return this;
    }

    /**
     * Get image
     * @return image
     **/
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public NodeCandidate hardware(Hardware hardware) {
        this.hardware = hardware;
        return this;
    }

    /**
     * Get hardware
     * @return hardware
     **/
    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public NodeCandidate location(Location location) {
        this.location = location;
        return this;
    }

    /**
     * Get location
     * @return location
     **/
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public NodeCandidate pricePerInvocation(Double pricePerInvocation) {
        this.pricePerInvocation = pricePerInvocation;
        return this;
    }

    /**
     * Get pricePerInvocation
     * @return pricePerInvocation
     **/
    public Double getPricePerInvocation() {
        return pricePerInvocation;
    }

    public void setPricePerInvocation(Double pricePerInvocation) {
        this.pricePerInvocation = pricePerInvocation;
    }

    public NodeCandidate memoryPrice(Double memoryPrice) {
        this.memoryPrice = memoryPrice;
        return this;
    }

    /**
     * Get memoryPrice
     * @return memoryPrice
     **/
    public Double getMemoryPrice() {
        return memoryPrice;
    }

    public void setMemoryPrice(Double memoryPrice) {
        this.memoryPrice = memoryPrice;
    }

    public NodeCandidate nodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    /**
     * Get nodeId
     * @return nodeId
     **/
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public NodeCandidate environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * Get environment
     * @return environment
     **/
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Check if a node candidate is of BYON type
     * @return true if yes, false if not
     */
    @JsonIgnore
    public boolean isByonNodeCandidate() {
        return nodeCandidateType.equals(NodeCandidateTypeEnum.BYON);
    }

    /**
     * Check if a node candidate is of BYON type
     * @return true if yes, false if not
     */
    @JsonIgnore
    public boolean isEdgeNodeCandidate() {
        return nodeCandidateType.equals(NodeCandidateTypeEnum.EDGE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeCandidate nodeCandidate = (NodeCandidate) o;
        return Objects.equals(this.id, nodeCandidate.id) &&
               Objects.equals(this.nodeCandidateType, nodeCandidate.nodeCandidateType) &&
               Objects.equals(this.jobIdForBYON, nodeCandidate.jobIdForBYON) &&
               Objects.equals(this.jobIdForEDGE, nodeCandidate.jobIdForEDGE) &&
               Objects.equals(this.price, nodeCandidate.price) && Objects.equals(this.cloud, nodeCandidate.cloud) &&
               Objects.equals(this.image, nodeCandidate.image) &&
               Objects.equals(this.hardware, nodeCandidate.hardware) &&
               Objects.equals(this.location, nodeCandidate.location) &&
               Objects.equals(this.pricePerInvocation, nodeCandidate.pricePerInvocation) &&
               Objects.equals(this.memoryPrice, nodeCandidate.memoryPrice) &&
               Objects.equals(this.nodeId, nodeCandidate.nodeId) &&
               Objects.equals(this.environment, nodeCandidate.environment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                            nodeCandidateType,
                            jobIdForBYON,
                            jobIdForEDGE,
                            price,
                            cloud,
                            image,
                            hardware,
                            location,
                            pricePerInvocation,
                            memoryPrice,
                            nodeId,
                            environment);
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
