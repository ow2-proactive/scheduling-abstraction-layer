/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.*;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * Represents a hardware offer by a cloud or edge device
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
    // Class name constant
    public static final String CLASS_NAME = "Hardware";

    // JSON property constants
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
        // A2 Series (Accelerator Optimized)
        gceGpuMappings.put("a2-highgpu-1g", 1);
        gceGpuMappings.put("a2-highgpu-2g", 2);
        gceGpuMappings.put("a2-highgpu-4g", 4);
        gceGpuMappings.put("a2-highgpu-8g", 8);
        gceGpuMappings.put("a2-megagpu-16g", 16);
        // A3 Series (Latest Accelerator Optimized)
        gceGpuMappings.put("a3-highgpu-1g", 1);
        gceGpuMappings.put("a3-highgpu-2g", 2);
        gceGpuMappings.put("a3-highgpu-4g", 4);
        gceGpuMappings.put("a3-highgpu-8g", 8);
        // G2 Series (Graphics Optimized)
        gceGpuMappings.put("g2-standard-4", 1);
        gceGpuMappings.put("g2-standard-8", 2);
        gceGpuMappings.put("g2-standard-16", 4);
        // N1 Series with GPUs
        gceGpuMappings.put("n1-standard with T4 GPU", 1);
        gceGpuMappings.put("n1-standard with P100", 1);
        gceGpuMappings.put("n1-standard with K80", 1);

        tempGpuMappings.put(CloudProviderType.GCE, Collections.unmodifiableMap(gceGpuMappings));

        // Azure GPU mappings
        Map<String, Integer> azureGpuMappings = new HashMap<>();
        // NC Series
        azureGpuMappings.put("Standard_NC6", 1);
        azureGpuMappings.put("Standard_NC12", 2);
        azureGpuMappings.put("Standard_NC24", 4);
        azureGpuMappings.put("Standard_NC24r", 4);
        azureGpuMappings.put("Standard_NC6ads_A100_v4", 1); // New: Minimum GPU count added
        azureGpuMappings.put("Standard_NC64as_T4_v3", 1); // New: Minimum GPU count added
        // ND Series
        azureGpuMappings.put("Standard_ND6s", 1);
        azureGpuMappings.put("Standard_ND12s", 2);
        azureGpuMappings.put("Standard_ND24s", 4);
        azureGpuMappings.put("Standard_ND24rs", 4);
        azureGpuMappings.put("Standard_ND40rs_v2", 1); // New: Minimum GPU count added
        azureGpuMappings.put("Standard_ND40rs_v4", 8);
        // NV Series
        azureGpuMappings.put("Standard_NV4as_v4", 1);
        azureGpuMappings.put("Standard_NV8as_v4", 2);
        azureGpuMappings.put("Standard_NV16as_v4", 4);
        azureGpuMappings.put("Standard_NV32as_v4", 4);
        azureGpuMappings.put("Standard_NV6s_v4", 1); // New: Minimum GPU count added
        // Additional instances
        azureGpuMappings.put("Standard_NDm_A100_v4", 8);
        azureGpuMappings.put("Standard_ND96asr_v5", 8);

        tempGpuMappings.put(CloudProviderType.AZURE, Collections.unmodifiableMap(azureGpuMappings));

        CLOUD_GPU_MAPPINGS = Collections.unmodifiableMap(tempGpuMappings);
    }

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
     * Custom toString() method for the Hardware class to format the output.
     * This method creates a formatted string representation of the Hardware object.
     * It uses a map of field names (represented as JSON constants) and their corresponding values
     * to build a human-readable string. The method leverages the {@link ModelUtils#buildToString}
     * utility method to generate the string, ensuring that all fields are included with proper formatting.
     *
     * @return A formatted string representation of the Hardware object, with each field on a new line.
     */
    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_ID, id);
        fields.put(JSON_NAME, name);
        fields.put(JSON_PROVIDER_ID, providerId);
        fields.put(JSON_CORES, cores);
        fields.put(JSON_CPU_FREQUENCY, cpuFrequency);
        fields.put(JSON_RAM, ram);
        fields.put(JSON_DISK, disk);
        fields.put(JSON_FPGA, fpga);
        fields.put(JSON_GPU, gpu);
        fields.put(JSON_LOCATION, location);
        fields.put(JSON_STATE, state);
        fields.put(JSON_OWNER, owner);

        return ModelUtils.buildToString(CLASS_NAME, fields);
    }

}
