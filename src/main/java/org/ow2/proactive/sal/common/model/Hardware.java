/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.sal.common.model;

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
               Objects.equals(this.location, hardware.location) && Objects.equals(this.state, hardware.state) &&
               Objects.equals(this.owner, hardware.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, providerId, cores, ram, disk, location, state, owner);
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
