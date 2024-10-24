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
    @Id
    @Column(name = "ID")
    @JsonProperty("id")
    private String id = null;

    @Column(name = "NAME")
    @JsonProperty("name")
    private String name = null;

    @Column(name = "PROVIDER_ID")
    @JsonProperty("providerId")
    private String providerId = null;

    @Column(name = "CORES")
    @JsonProperty("cores")
    private Integer cores = null;

    @Column(name = "RAM")
    @JsonProperty("ram")
    private Long ram = null;

    @Column(name = "DISK")
    @JsonProperty("disk")
    private Double disk = null;

    @Column(name = "FPGA")
    @JsonProperty("fpga")
    private Integer fpga = null;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonProperty("location")
    private Location location = null;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    @JsonProperty("state")
    private DiscoveryItemState state = null;

    @Column(name = "OWNER")
    @JsonProperty("owner")
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
               Objects.equals(this.fpga, hardware.fpga) && Objects.equals(this.location, hardware.location) &&
               Objects.equals(this.state, hardware.state) && Objects.equals(this.owner, hardware.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, providerId, cores, ram, disk, fpga, location, state, owner);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Hardware {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
        sb.append("    cores: ").append(toIndentedString(cores)).append("\n");
        sb.append("    ram: ").append(toIndentedString(ram)).append("\n");
        sb.append("    disk: ").append(toIndentedString(disk)).append("\n");
        sb.append("    fpga: ").append(toIndentedString(fpga)).append("\n");
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
