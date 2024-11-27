/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Attributes defining this node
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@Embeddable
public class NodeProperties implements Serializable {

    // Fields - edge node properties fields are mapped to node candidates so field names are reused
    @JsonProperty(Hardware.JSON_PROVIDER_ID)
    private String providerId = null;

    @JsonProperty(NodeCandidate.JSON_PRICE)
    private Double price = null;

    @JsonProperty(Hardware.JSON_CORES)
    private Integer cores = null;

    @JsonProperty(Hardware.JSON_CPU_FREQUENCY)
    private Double cpuFrequency = null;

    @JsonProperty(Hardware.JSON_RAM)
    private Long ram = null;

    @JsonProperty(Hardware.JSON_DISK)
    private Double disk = null;

    @JsonProperty(Hardware.JSON_FPGA)
    private Integer fpga = null;

    @JsonProperty(Hardware.JSON_GPU)
    private Integer gpu = null;

    @Embedded
    @JsonProperty(Image.JSON_OPERATING_SYSTEM)
    private OperatingSystem operatingSystem = null;

    @Embedded
    @JsonProperty(Location.JSON_GEO_LOCATION)
    private GeoLocation geoLocation = null;

    /**
     * Custom toString method with indentation and field labels.
     * It creates a more readable string output for debugging and logging.
     *
     * @return a string representation of the NodeProperties instance.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NodeProperties {\n");
        sb.append("    ")
          .append(Hardware.JSON_PROVIDER_ID)
          .append(": ")
          .append(toIndentedString(providerId))
          .append("\n");
        sb.append("    ").append(NodeCandidate.JSON_PRICE).append(": ").append(toIndentedString(price)).append("\n");
        sb.append("    ").append(Hardware.JSON_CORES).append(": ").append(toIndentedString(cores)).append("\n");
        sb.append("    ")
          .append(Hardware.JSON_CPU_FREQUENCY)
          .append(": ")
          .append(toIndentedString(cpuFrequency))
          .append("\n");
        sb.append("    ").append(Hardware.JSON_RAM).append(": ").append(toIndentedString(ram)).append("\n");
        sb.append("    ").append(Hardware.JSON_DISK).append(": ").append(toIndentedString(disk)).append("\n");
        sb.append("    ").append(Hardware.JSON_FPGA).append(": ").append(toIndentedString(fpga)).append("\n");
        sb.append("    ").append(Hardware.JSON_GPU).append(": ").append(toIndentedString(gpu)).append("\n");
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
     * Helper method to convert objects to indented strings.
     * @param obj The object to convert.
     * @return A string representation of the object or "null" if the object is null.
     */
    private String toIndentedString(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.toString().replace("\n", "\n    ");
    }
}
