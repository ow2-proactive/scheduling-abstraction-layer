/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import org.ow2.proactive.sal.util.ModelUtils;

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
     * Custom toString() method for the class to format the output.
     * This method creates a formatted string representation of the class object.
     * It uses a map of field names (represented as JSON constants) and their corresponding values
     * to build a human-readable string. The method leverages the {@link ModelUtils#buildToString}
     * utility method to generate the string, ensuring that all fields are included with proper formatting.
     *
     * @return A formatted string representation of the Hardware object, with each field on a new line.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(Hardware.JSON_PROVIDER_ID, providerId);
        fields.put(NodeCandidate.JSON_PRICE, price);
        fields.put(Hardware.JSON_CORES, cores);
        fields.put(Hardware.JSON_CPU_FREQUENCY, cpuFrequency);
        fields.put(Hardware.JSON_RAM, ram);
        fields.put(Hardware.JSON_DISK, disk);
        fields.put(Hardware.JSON_FPGA, fpga);
        fields.put(Hardware.JSON_GPU, gpu);
        fields.put(Image.JSON_OPERATING_SYSTEM, operatingSystem);
        fields.put(Location.JSON_GEO_LOCATION, geoLocation);

        return ModelUtils.buildToString(getClass().getSimpleName(), fields);
    }
}
