/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Represents the operating system of an image
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class OperatingSystem implements Serializable {
    @Column(name = "OPERATING_SYSTEM_FAMILY")
    @Enumerated(EnumType.STRING)
    @JsonProperty("operatingSystemFamily")
    private OperatingSystemFamily operatingSystemFamily = null;

    @Column(name = "OPERATING_SYSTEM_ARCHITECTURE")
    @Enumerated(EnumType.STRING)
    @JsonProperty("operatingSystemArchitecture")
    private OperatingSystemArchitecture operatingSystemArchitecture = null;

    @Column(name = "OPERATING_SYSTEM_VERSION")
    @JsonProperty("operatingSystemVersion")
    private BigDecimal operatingSystemVersion = null;

    public OperatingSystem operatingSystemFamily(OperatingSystemFamily operatingSystemFamily) {
        this.operatingSystemFamily = operatingSystemFamily;
        return this;
    }

    /**
     * Get operatingSystemFamily
     * @return operatingSystemFamily
     **/
    public OperatingSystemFamily getOperatingSystemFamily() {
        return operatingSystemFamily;
    }

    public void setOperatingSystemFamily(OperatingSystemFamily operatingSystemFamily) {
        this.operatingSystemFamily = operatingSystemFamily;
    }

    public OperatingSystem operatingSystemArchitecture(OperatingSystemArchitecture operatingSystemArchitecture) {
        this.operatingSystemArchitecture = operatingSystemArchitecture;
        return this;
    }

    /**
     * Get operatingSystemArchitecture
     * @return operatingSystemArchitecture
     **/
    public OperatingSystemArchitecture getOperatingSystemArchitecture() {
        return operatingSystemArchitecture;
    }

    public void setOperatingSystemArchitecture(OperatingSystemArchitecture operatingSystemArchitecture) {
        this.operatingSystemArchitecture = operatingSystemArchitecture;
    }

    public OperatingSystem operatingSystemVersion(BigDecimal operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
        return this;
    }

    /**
     * Version of the OS
     * @return operatingSystemVersion
     **/
    public BigDecimal getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(BigDecimal operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperatingSystem operatingSystem = (OperatingSystem) o;
        return Objects.equals(this.operatingSystemFamily, operatingSystem.operatingSystemFamily) &&
               Objects.equals(this.operatingSystemArchitecture, operatingSystem.operatingSystemArchitecture) &&
               Objects.equals(this.operatingSystemVersion, operatingSystem.operatingSystemVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operatingSystemFamily, operatingSystemArchitecture, operatingSystemVersion);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OperatingSystem {\n");

        sb.append("    operatingSystemFamily: ").append(toIndentedString(operatingSystemFamily)).append("\n");
        sb.append("    operatingSystemArchitecture: ")
          .append(toIndentedString(operatingSystemArchitecture))
          .append("\n");
        sb.append("    operatingSystemVersion: ").append(toIndentedString(operatingSystemVersion)).append("\n");
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
