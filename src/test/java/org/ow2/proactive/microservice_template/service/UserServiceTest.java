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
package org.ow2.proactive.microservice_template.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.ow2.proactive.microservice_template.fixtures.UserFixture;
import org.ow2.proactive.microservice_template.model.User;


/**
 * @author ActiveEon Team on 4/29/2016.
 */
public class UserServiceTest {

    private User user;

    private Optional<User> optUser;

    private UserService userService;

    @Before
    public void init() {
        user = UserFixture.simpleUser();
        optUser = Optional.of(user);
        userService = new UserService();
    }

    @Test
    public void testSaveUser() {
        userService.saveUser(user);
        assertThat(userService.findAllUsers().size(), is(1));

        userService.saveUser(new User());
        assertThat(userService.findAllUsers().size(), is(2));
    }

    @Test
    public void testFindByName() {
        userService.saveUser(user);

        assertThat(userService.findByName(user.getName()), is(optUser));
        assertThat(userService.findByName(""), is(Optional.empty()));
    }

    @Test
    public void findAllUsers() {
        userService.saveUser(user);

        assertThat(userService.findAllUsers().iterator().next(), is(user));
    }

    @Test
    public void testUpdateUser() {
        userService.saveUser(user);

        user.setAge(1);
        userService.updateUser(user);
        assertThat(userService.findByName(user.getName()), is(optUser));
    }

    @Test
    public void testDeleteUserByName() {
        userService.deleteUserByName(user.getName());
        assertThat(userService.findByName(user.getName()), is(Optional.empty()));
    }

    @Test
    public void deleteAllUsers() {
        userService.saveUser(user);

        userService.deleteAllUsers();
        assertThat(userService.findAllUsers().size(), is(0));
    }
}
