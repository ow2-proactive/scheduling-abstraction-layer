/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * Represents an API used by a cloud
 */
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Api implements Serializable {
    /*
     * Possible values of providerName for particular CSPs:
     * AWS EC2 = "aws-ec2"
     * Openstack = "openstack4j"
     * Google = "google-compute-engine"
     * Azure = "azure"
     * Oktawave = "oktawave"
     */
    @Column(name = "PROVIDER_NAME")
    @JsonProperty("providerName")
    private String providerName = null;

    public Api providerName(String providerName) {
        this.providerName = providerName;
        return this;
    }

    /**
     * Name of the API provider, maps to a driver
     * @return providerName
     **/
    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Api api = (Api) o;
        return Objects.equals(this.providerName, api.providerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Api {\n");

        sb.append("    providerName: ").append(toIndentedString(providerName)).append("\n");
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
