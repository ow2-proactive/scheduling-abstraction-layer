/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Represents a hardware offer by a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@Entity
@Table(name = "HARDWARE")
public class Hardware implements Serializable {

    // FPGA mappings for cloud providers
    private static final Map<CloudProviderType, Map<String, Integer>> CLOUD_FPGA_MAPPINGS;
    static {
        Map<CloudProviderType, Map<String, Integer>> tempMappings = new HashMap<>();

        // AWS mappings
        Map<String, Integer> awsMappings = new HashMap<>();
        awsMappings.put("f1.2xlarge", 1);
        awsMappings.put("f1.4xlarge", 2);
        awsMappings.put("f1.16xlarge", 8);
        tempMappings.put(CloudProviderType.AWS_EC2, Collections.unmodifiableMap(awsMappings));

        // GCE mappings
        Map<String, Integer> gceMappings = new HashMap<>();
        gceMappings.put("n1-standard-1", 0);
        gceMappings.put("n1-standard-2", 0);
        gceMappings.put("a2-highgpu-1g", 1);
        gceMappings.put("a2-highgpu-8g", 8);
        tempMappings.put(CloudProviderType.GCE, Collections.unmodifiableMap(gceMappings));

        // Azure mappings
        Map<String, Integer> azureMappings = new HashMap<>();
        azureMappings.put("Standard_NC6", 0);
        azureMappings.put("Standard_ND6s", 1);
        azureMappings.put("Standard_ND24s", 4);
        tempMappings.put(CloudProviderType.AZURE, Collections.unmodifiableMap(azureMappings));

        CLOUD_FPGA_MAPPINGS = Collections.unmodifiableMap(tempMappings);
    }

    public static final String JSON_ID = "id";

    public static final String JSON_NAME = "name";

    public static final String JSON_PROVIDER_ID = "providerId";

    public static final String JSON_CORES = "cores";

    public static final String JSON_CPU_FREQUENCY = "cpuFrequency";

    public static final String JSON_RAM = "ram";

    public static final String JSON_DISK = "disk";

    public static final String JSON_FPGA = "fpga";

    public static final String JSON_GPU = "gpu";

    public static final String JSON_LOCATION = "location";

    public static final String JSON_STATE = "state";

    public static final String JSON_OWNER = "owner";

    @Id
    @Column(name = "ID")
    @JsonProperty(JSON_ID)
    private String id = null;

    @Column(name = "NAME")
    @JsonProperty(JSON_NAME)
    private String name = null;

    @Column(name = "PROVIDER_ID")
    @JsonProperty(JSON_PROVIDER_ID)
    private String providerId = null;

    @Column(name = "CORES")
    @JsonProperty(JSON_CORES)
    private Integer cores = null;

    @Column(name = "CPU_FREQUENCY")
    @JsonProperty(JSON_CPU_FREQUENCY)
    private Double cpuFrequency = null;

    @Column(name = "RAM")
    @JsonProperty(JSON_RAM)
    private Long ram = null;

    @Column(name = "DISK")
    @JsonProperty(JSON_DISK)
    private Double disk = null;

    @Column(name = "FPGA")
    @JsonProperty(JSON_FPGA)
    private Integer fpga = null;

    @Column(name = "GPU")
    @JsonProperty(JSON_GPU)
    private Integer gpu = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonProperty(JSON_LOCATION)
    private Location location = null;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_STATE)
    private DiscoveryItemState state = null;

    @Column(name = "OWNER")
    @JsonProperty(JSON_OWNER)
    private String owner = null;

    /**
     * Sets the FPGA field based on machine type.
     * @param machineType the machine type
    
    public void setCloudFpga(String machineType) {
        switch (machineType) {
            case "f1.2xlarge":
                this.fpga = 1;
                break;
            case "f1.4xlarge":
                this.fpga = 2;
                break;
            case "f1.16xlarge":
                this.fpga = 8;
                break;
            default:
                this.fpga = 0;
        }
    }*/

    /**
     * Sets the FPGA field based on the cloud provider and machine type.
     * @param cloudProvider The type of the cloud provider (as a CloudProviderType enum).
     * @param machineType The machine type to map.
     */
    public void setCloudFpga(CloudProviderType cloudProvider, String machineType) {
        Map<String, Integer> cloudMapping = CLOUD_FPGA_MAPPINGS.get(cloudProvider);
        if (cloudMapping != null) {
            this.fpga = cloudMapping.getOrDefault(machineType, 0);
        } else {
            throw new IllegalArgumentException("Unsupported cloud provider: " + cloudProvider);
        }
    }

    /**
     * Custom toString() method for the Hardware class to format the output
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Hardware {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
        sb.append("    cores: ").append(toIndentedString(cores)).append("\n");
        sb.append("    cpuFrequency: ").append(toIndentedString(cpuFrequency)).append("\n");
        sb.append("    ram: ").append(toIndentedString(ram)).append("\n");
        sb.append("    disk: ").append(toIndentedString(disk)).append("\n");
        sb.append("    fpga: ").append(toIndentedString(fpga)).append("\n");
        sb.append("    gpu: ").append(toIndentedString(gpu)).append("\n");
        sb.append("    location: ").append(toIndentedString(location)).append("\n");
        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    owner: ").append(toIndentedString(owner)).append("\n");
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
