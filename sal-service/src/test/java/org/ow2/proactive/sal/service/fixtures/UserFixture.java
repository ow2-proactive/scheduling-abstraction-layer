/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.fixtures;

import org.ow2.proactive.sal.model.User;


/**
 * Created by Iaroslav on 4/29/2016.
 */
public class UserFixture {

    public static User simpleUser() {
        return new User("Marco", 18, 70000);
    }
}
