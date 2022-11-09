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
package org.ow2.proactive.sal.service.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Represents an image offer by a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "IMAGE")
public class Image implements Serializable {
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

    @Embedded
    @JsonProperty("operatingSystem")
    private OperatingSystem operatingSystem = null;

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

    public Image id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier for this image
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Human-readable name
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image providerId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    /**
     * Original id issued by provider
     * @return providerId
     **/
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Image operatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
        return this;
    }

    /**
     * Get operatingSystem
     * @return operatingSystem
     **/
    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public Image location(Location location) {
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

    public Image state(DiscoveryItemState state) {
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

    public Image owner(String owner) {
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
        Image image = (Image) o;
        return Objects.equals(this.id, image.id) && Objects.equals(this.name, image.name) &&
               Objects.equals(this.providerId, image.providerId) &&
               Objects.equals(this.operatingSystem, image.operatingSystem) &&
               Objects.equals(this.location, image.location) && Objects.equals(this.state, image.state) &&
               Objects.equals(this.owner, image.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, providerId, operatingSystem, location, state, owner);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Image {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
        sb.append("    operatingSystem: ").append(toIndentedString(operatingSystem)).append("\n");
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
