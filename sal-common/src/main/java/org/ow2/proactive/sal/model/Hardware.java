/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Represents a hardware offer by a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "HARDWARE")
public class Hardware implements Serializable {
    // Constants for JSON property names
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

    public Hardware id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier for the hardware
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Hardware name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Human-readable name for the hardware
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hardware providerId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    /**
     * Original id issued by the provider
     * @return providerId
     **/
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Hardware cores(Integer cores) {
        this.cores = cores;
        return this;
    }

    /**
     * Number of cores
     * @return cores
     **/
    public Integer getCores() {
        return cores;
    }

    public void setCores(Integer cores) {
        this.cores = cores;
    }

    public Hardware cpuFrequency(Double cpuFrequency) {
        this.cpuFrequency = cpuFrequency;
        return this;
    }

    /**
     * Sets the CPU frequency in GHz.
     * @param cpuFrequency CPU frequency in GHz
     */
    public void setCpuFrequency(Double cpuFrequency) {
        this.cpuFrequency = cpuFrequency;
    }

    /**
     * Gets the CPU frequency in GHz.
     * @return cpuFrequency
     */
    public Double getCpuFrequency() {
        return cpuFrequency;
    }

    public Hardware ram(Long ram) {
        this.ram = ram;
        return this;
    }

    /**
     * Amount of RAM (in MB)
     * @return ram
     **/
    public Long getRam() {
        return ram;
    }

    public void setRam(Long ram) {
        this.ram = ram;
    }

    public Integer getFpga() {
        return fpga;
    }

    public void setFpga(Integer fpga) {
        this.fpga = fpga;
    }

    public void setFpga(String machineType) {
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
    }

    public Hardware gpu(Integer gpu) {
        this.gpu = gpu;
        return this;
    }

    /**
     * Sets the number of GPUs.
     * @param gpu Number of GPUs
     */
    public void setGpu(Integer gpu) {
        this.gpu = gpu;
    }

    /**
     * Gets the number of GPUs.
     * @return gpu
     */
    public Integer getGpu() {
        return gpu;
    }

    public Hardware disk(Double disk) {
        this.disk = disk;
        return this;
    }

    /**
     * Amount of disk space (in GB)
     * @return disk
     **/
    public Double getDisk() {
        return disk;
    }

    public void setDisk(Double disk) {
        this.disk = disk;
    }

    public Hardware location(Location location) {
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

    public Hardware state(DiscoveryItemState state) {
        this.state = state;
        return this;
    }

    /**
     * Get state
     * @return state
     **/
    public DiscoveryItemState getState() {
        return state;
    }

    public void setState(DiscoveryItemState state) {
        this.state = state;
    }

    public Hardware owner(String owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Get owner
     * @return owner
     **/
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hardware hardware = (Hardware) o;
        return Objects.equals(this.id, hardware.id) && Objects.equals(this.name, hardware.name) &&
               Objects.equals(this.providerId, hardware.providerId) && Objects.equals(this.cores, hardware.cores) &&
               Objects.equals(this.ram, hardware.ram) && Objects.equals(this.disk, hardware.disk) &&
               Objects.equals(this.fpga, hardware.fpga) && Objects.equals(this.gpu, hardware.gpu) && // Added GPU comparison
               Objects.equals(this.cpuFrequency, hardware.cpuFrequency) && // Added CPU frequency comparison
               Objects.equals(this.location, hardware.location) && Objects.equals(this.state, hardware.state) &&
               Objects.equals(this.owner, hardware.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, providerId, cores, ram, disk, fpga, gpu, cpuFrequency, location, state, owner);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Hardware {\n");
        sb.append("    ").append(JSON_ID).append(": ").append(toIndentedString(id)).append("\n");
        sb.append("    ").append(JSON_NAME).append(": ").append(toIndentedString(name)).append("\n");
        sb.append("    ").append(JSON_PROVIDER_ID).append(": ").append(toIndentedString(providerId)).append("\n");
        sb.append("    ").append(JSON_CORES).append(": ").append(toIndentedString(cores)).append("\n");
        sb.append("    ").append(JSON_RAM).append(": ").append(toIndentedString(ram)).append("\n");
        sb.append("    ").append(JSON_DISK).append(": ").append(toIndentedString(disk)).append("\n");
        sb.append("    ").append(JSON_FPGA).append(": ").append(toIndentedString(fpga)).append("\n");
        sb.append("    ").append(JSON_GPU).append(": ").append(toIndentedString(gpu)).append("\n");
        sb.append("    ").append(JSON_CPU_FREQUENCY).append(": ").append(toIndentedString(cpuFrequency)).append("\n");
        sb.append("    ").append(JSON_LOCATION).append(": ").append(toIndentedString(location)).append("\n");
        sb.append("    ").append(JSON_STATE).append(": ").append(toIndentedString(state)).append("\n");
        sb.append("    ").append(JSON_OWNER).append(": ").append(toIndentedString(owner)).append("\n");
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
