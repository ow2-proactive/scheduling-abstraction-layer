/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.ow2.proactive.sal.model.User;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserService {

    private Map<String, User> users = new HashMap<>();

    public Collection<User> findAllUsers() {
        return users.values();
    }

    public Optional<User> findByName(String name) {
        return Optional.ofNullable(users.get(name));
    }

    public void saveUser(User user) {
        users.put(user.getName(), user);
    }

    public void updateUser(User user) {
        users.put(user.getName(), user);
    }

    public void deleteUserByName(String name) {
        users.remove(name);
    }

    public void deleteAllUsers() {
        users.clear();
    }

}
