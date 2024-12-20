/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.ow2.proactive.sal.util.ModelUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * IpAddress
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@Embeddable
public class IpAddress implements Serializable {

    // JSON field constants
    public static final String JSON_IP_ADDRESS_TYPE = "IpAddressType";

    public static final String JSON_IP_VERSION = "IpVersion";

    public static final String JSON_VALUE = "value";

    @Column(name = "IP_ADDRESS_TYPE")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_IP_ADDRESS_TYPE)
    private IpAddressType ipAddressType = null;

    @Column(name = "IP_VERSION")
    @Enumerated(EnumType.STRING)
    @JsonProperty(JSON_IP_VERSION)
    private IpVersion ipVersion = null;

    @Column(name = "VALUE")
    @JsonProperty(JSON_VALUE)
    private String value = null;

    @Override
    public String toString() {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(JSON_IP_ADDRESS_TYPE, ipAddressType);
        fields.put(JSON_IP_VERSION, ipVersion);
        fields.put(JSON_VALUE, value);
        return ModelUtils.buildToString(getClass().getSimpleName(), fields);
    }
}
