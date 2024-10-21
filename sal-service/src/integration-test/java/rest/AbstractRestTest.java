/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package rest;

import org.springframework.beans.factory.annotation.Value;


/**
 * @author ActiveEon Team on 5/2/2016.
 */
public class AbstractRestTest {
    @Value("${local.server.port}")
    protected int serverPort;
}
