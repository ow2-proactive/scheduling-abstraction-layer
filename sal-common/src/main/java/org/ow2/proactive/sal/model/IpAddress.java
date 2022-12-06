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
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * IpAddress
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class IpAddress implements Serializable {
    @Column(name = "IP_ADDRESS_TYPE")
    @Enumerated(EnumType.STRING)
    @JsonProperty("IpAddressType")
    private IpAddressType ipAddressType = null;

    @Column(name = "IP_VERSION")
    @Enumerated(EnumType.STRING)
    @JsonProperty("IpVersion")
    private IpVersion ipVersion = null;

    @Column(name = "VALUE")
    @JsonProperty("value")
    private String value = null;

    public IpAddress ipAddressType(IpAddressType ipAddressType) {
        this.ipAddressType = ipAddressType;
        return this;
    }

    /**
     * Get ipAddressType
     * @return ipAddressType
     **/
    @NotNull
    public IpAddressType getIpAddressType() {
        return ipAddressType;
    }

    public void setIpAddressType(IpAddressType ipAddressType) {
        this.ipAddressType = ipAddressType;
    }

    public IpAddress ipVersion(IpVersion ipVersion) {
        this.ipVersion = ipVersion;
        return this;
    }

    /**
     * Get ipVersion
     * @return ipVersion
     **/
    @NotNull
    public IpVersion getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(IpVersion ipVersion) {
        this.ipVersion = ipVersion;
    }

    public IpAddress value(String value) {
        this.value = value;
        return this;
    }

    /**
     * the ip address value
     * @return value
     **/
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IpAddress ipAddress = (IpAddress) o;
        return Objects.equals(this.ipAddressType, ipAddress.ipAddressType) &&
               Objects.equals(this.ipVersion, ipAddress.ipVersion) && Objects.equals(this.value, ipAddress.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddressType, ipVersion, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IpAddress {\n");

        sb.append("    ipAddressType: ").append(toIndentedString(ipAddressType)).append("\n");
        sb.append("    ipVersion: ").append(toIndentedString(ipVersion)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
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
