/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Attributes defining this node
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class NodeProperties implements Serializable {
    @JsonProperty("price")
    private Double price = null;

    @JsonProperty("providerId")
    private String providerId = null;

    @JsonProperty("numberOfCores")
    private Integer numberOfCores = null;

    @JsonProperty("memory")
    private Long memory = null;

    @JsonProperty("disk")
    private Float disk = null;

    @Embedded
    @JsonProperty("operatingSystem")
    private OperatingSystem operatingSystem = null;

    @Embedded
    @JsonProperty("geoLocation")
    private GeoLocation geoLocation = null;

    public NodeProperties providerId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    /**
     * Id of the provider where this node is managed. For virtual machines this e.g. the id of the cloud.
     * @return providerId
     **/
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public NodeProperties numberOfCores(Integer numberOfCores) {
        this.numberOfCores = numberOfCores;
        return this;
    }

    /**
     * Number of cores the node has.
     * @return numberOfCores
     **/
    public Integer getNumberOfCores() {
        return numberOfCores;
    }

    public void setNumberOfCores(Integer numberOfCores) {
        this.numberOfCores = numberOfCores;
    }

    public NodeProperties memory(Long memory) {
        this.memory = memory;
        return this;
    }

    /**
     * Amount of RAM this node has (in MB).
     * @return memory
     **/
    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }

    public NodeProperties disk(Float disk) {
        this.disk = disk;
        return this;
    }

    /**
     * Amount of disk space this node has (in GB).
     * @return disk
     **/
    public Float getDisk() {
        return disk;
    }

    public void setDisk(Float disk) {
        this.disk = disk;
    }

    public NodeProperties operatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
        return this;
    }

    /**
     * Operating system of this node.
     * @return operatingSystem
     **/
    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public NodeProperties geoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
        return this;
    }

    /**
     * Geographical location this node resides in.
     * @return geoLocation
     **/
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeProperties nodeProperties = (NodeProperties) o;
        return Objects.equals(this.providerId, nodeProperties.providerId) &&
               Objects.equals(this.numberOfCores, nodeProperties.numberOfCores) &&
               Objects.equals(this.memory, nodeProperties.memory) && Objects.equals(this.disk, nodeProperties.disk) &&
               Objects.equals(this.operatingSystem, nodeProperties.operatingSystem) &&
               Objects.equals(this.geoLocation, nodeProperties.geoLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, numberOfCores, memory, disk, operatingSystem, geoLocation);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NodeProperties {\n");

        sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
        sb.append("    numberOfCores: ").append(toIndentedString(numberOfCores)).append("\n");
        sb.append("    memory: ").append(toIndentedString(memory)).append("\n");
        sb.append("    disk: ").append(toIndentedString(disk)).append("\n");
        sb.append("    operatingSystem: ").append(toIndentedString(operatingSystem)).append("\n");
        sb.append("    geoLocation: ").append(toIndentedString(geoLocation)).append("\n");
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
