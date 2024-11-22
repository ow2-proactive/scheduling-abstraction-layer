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

    // Fields
    @JsonProperty(Hardware.JSON_PROVIDER_ID)
    private String providerId = null;

    @JsonProperty(NodeCandidate.JSON_PRICE)
    private Double price = null;

    @JsonProperty(Hardware.JSON_CORES)
    private Integer cores = null;

    @JsonProperty(Hardware.JSON_RAM)
    private Long memory = null;

    @JsonProperty(Hardware.JSON_DISK)
    private Double disk = null;

    @Embedded
    @JsonProperty(Image.JSON_OPERATING_SYSTEM)
    private OperatingSystem operatingSystem = null;

    @Embedded
    @JsonProperty(Location.JSON_GEO_LOCATION)
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
        this.cores = numberOfCores;
        return this;
    }

    /**
     * Number of cores the node has.
     * @return numberOfCores
     **/
    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer numberOfCores) {
        this.cores = numberOfCores;
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

    public NodeProperties disk(Double disk) {
        this.disk = disk;
        return this;
    }

    /**
     * Amount of disk space this node has (in GB).
     * @return disk
     **/
    public Double getDisk() {
        return disk;
    }

    public void setDisk(Double disk) {
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
               Objects.equals(this.price, nodeProperties.price) &&
               Objects.equals(this.cores, nodeProperties.cores) &&
               Objects.equals(this.memory, nodeProperties.memory) && Objects.equals(this.disk, nodeProperties.disk) &&
               Objects.equals(this.operatingSystem, nodeProperties.operatingSystem) &&
               Objects.equals(this.geoLocation, nodeProperties.geoLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, cores, memory, disk, operatingSystem, geoLocation);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NodeProperties {\n");

        sb.append("    ").append(Hardware.JSON_PROVIDER_ID).append(": ").append(toIndentedString(providerId)).append("\n");
        sb.append("    ").append(NodeCandidate.JSON_PRICE).append(": ").append(toIndentedString(price)).append("\n");
        sb.append("    ").append(Hardware.JSON_CORES).append(": ").append(toIndentedString(cores)).append("\n");
        sb.append("    ").append(Hardware.JSON_RAM).append(": ").append(toIndentedString(memory)).append("\n");
        sb.append("    ").append(Hardware.JSON_DISK).append(": ").append(toIndentedString(disk)).append("\n");
        sb.append("    ")
          .append(Image.JSON_OPERATING_SYSTEM)
          .append(": ")
          .append(toIndentedString(operatingSystem))
          .append("\n");
        sb.append("    ")
          .append(Location.JSON_GEO_LOCATION)
          .append(": ")
          .append(toIndentedString(geoLocation))
          .append("\n");

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
