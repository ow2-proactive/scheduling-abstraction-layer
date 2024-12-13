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

        CLOUD_FPGA_MAPPINGS = Collections.unmodifiableMap(tempMappings);
    }

    // GPU mappings for cloud providers
    private static final Map<CloudProviderType, Map<String, Integer>> CLOUD_GPU_MAPPINGS;
    static {
        Map<CloudProviderType, Map<String, Integer>> tempGpuMappings = new HashMap<>();

        // AWS GPU mappings
        Map<String, Integer> awsGpuMappings = new HashMap<>();
        // P-Series Instances
        awsGpuMappings.put("p3.2xlarge", 1);
        awsGpuMappings.put("p3.8xlarge", 4);
        awsGpuMappings.put("p3.16xlarge", 8);
        awsGpuMappings.put("p3dn.24xlarge", 8);
        awsGpuMappings.put("p4d.24xlarge", 8);
        awsGpuMappings.put("p4de.24xlarge", 8);
        awsGpuMappings.put("p5e.48xlarge", 8);
        awsGpuMappings.put("p5en.48xlarge", 8);
        // G-Series Instances
        awsGpuMappings.put("g4dn.xlarge", 1);
        awsGpuMappings.put("g4dn.2xlarge", 1);
        awsGpuMappings.put("g4dn.4xlarge", 1);
        awsGpuMappings.put("g4dn.8xlarge", 1);
        awsGpuMappings.put("g4dn.12xlarge", 4);
        awsGpuMappings.put("g4dn.16xlarge", 4);
        awsGpuMappings.put("g5.xlarge", 1);
        awsGpuMappings.put("g5.2xlarge", 1);
        awsGpuMappings.put("g5.4xlarge", 1);
        awsGpuMappings.put("g5.8xlarge", 1);
        awsGpuMappings.put("g5.12xlarge", 4);
        awsGpuMappings.put("g5.16xlarge", 4);
        awsGpuMappings.put("g5.24xlarge", 4);
        awsGpuMappings.put("g5.48xlarge", 8);
        awsGpuMappings.put("g6e.xlarge", 1);
        awsGpuMappings.put("g6e.2xlarge", 1);
        awsGpuMappings.put("g6e.4xlarge", 1);
        awsGpuMappings.put("g6e.8xlarge", 1);
        awsGpuMappings.put("g6e.12xlarge", 4);
        awsGpuMappings.put("g6e.24xlarge", 4);
        awsGpuMappings.put("g6e.48xlarge", 8);

        tempGpuMappings.put(CloudProviderType.AWS_EC2, Collections.unmodifiableMap(awsGpuMappings));

        // GCE GPU mappings
        Map<String, Integer> gceGpuMappings = new HashMap<>();
        gceGpuMappings.put("a2-megagpu-16g", 16);
        gceGpuMappings.put("a2-highgpu-8g", 8);
        gceGpuMappings.put("a2-highgpu-1g", 1);
        gceGpuMappings.put("n1-standard with T4 GPU", 1);
        gceGpuMappings.put("n1-standard with P100", 1);
        gceGpuMappings.put("n1-standard with K80", 1);
        tempGpuMappings.put(CloudProviderType.GCE, Collections.unmodifiableMap(gceGpuMappings));

        // Azure GPU mappings
        Map<String, Integer> azureGpuMappings = new HashMap<>();
        azureGpuMappings.put("Standard_NC6", 1);
        azureGpuMappings.put("Standard_NC6ads_A100_v4", 1);
        azureGpuMappings.put("Standard_NV6s_v4", 1);
        azureGpuMappings.put("Standard_ND6s", 1);
        azureGpuMappings.put("Standard_NC12", 2);
        azureGpuMappings.put("Standard_NC24", 4);
        azureGpuMappings.put("Standard_NC64as_T4_v3", 4);
        azureGpuMappings.put("Standard_ND40rs_v2", 8);
        tempGpuMappings.put(CloudProviderType.AZURE, Collections.unmodifiableMap(azureGpuMappings));

        CLOUD_GPU_MAPPINGS = Collections.unmodifiableMap(tempGpuMappings);
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
     * Sets the FPGA field based on the cloud provider and machine type.
     * @param cloudProvider The type of the cloud provider (as a CloudProviderType enum).
     * @param machineType The machine type to map.
     */
    public void setCloudFpga(CloudProviderType cloudProvider, String machineType) {
        Map<String, Integer> cloudMapping = CLOUD_FPGA_MAPPINGS.get(cloudProvider);
        if (cloudMapping != null) {
            this.fpga = cloudMapping.getOrDefault(machineType, 0);
        } else {
            this.fpga = null;
        }
    }

    /**
     * Sets the GPU field based on the cloud provider and machine type.
     * @param cloudProvider The type of the cloud provider (as a CloudProviderType enum).
     * @param machineType The machine type to map.
     */
    public void setCloudGpu(CloudProviderType cloudProvider, String machineType) {
        Map<String, Integer> gpuMapping = CLOUD_GPU_MAPPINGS.get(cloudProvider);
        if (gpuMapping != null) {
            this.gpu = gpuMapping.getOrDefault(machineType, 0);
        } else {
            this.gpu = null;
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
