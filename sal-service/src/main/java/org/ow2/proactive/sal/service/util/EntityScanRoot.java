/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.util;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;


/**
 * Allows to override the entity scan root
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EntityScanRootRegistrar.class)
public @interface EntityScanRoot {

    /**
     * Override the default entity scan root ("classpath:") to a more accurate path.
     * This expression is parsed by a PathMatchingResourcePatternResolver.
     *
     * Supports wild card notation e.g. classpath*:path/somejar*.jar. The wildcard must return a single resource
     *
     * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResource(String)
     * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver#getResources(String)
     *
     * @return the entity scan classpath string
     */
    String value() default "classpath:";

}
