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
package org.ow2.proactive.microservice_template.rest;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ow2.proactive.microservice_template.model.User;
import org.ow2.proactive.microservice_template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Implement CRUD methods for REST service
 */

@RestController
@RequestMapping(value = "/users/")
public class UserRest {

    private final Logger logger = LogManager.getRootLogger();

    @Autowired
    private UserService userService;

    //-------------------Retrieve All Users--------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Collection<User>> listAllUsers() {
        Collection<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //-------------------Retrieve Single User--------------------------------------------------------

    @RequestMapping(value = "{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@PathVariable("name") String name) {
        logger.debug("Fetching User with name " + name);
        return userService.findByName(name)
                          .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                          .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    //-------------------Create a User--------------------------------------------------------

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody User user) {
        logger.debug("Creating User " + user.getName());
        return userService.findByName(user.getName())
                          .map(userFound -> new ResponseEntity<>(userFound, HttpStatus.CONFLICT))
                          .orElseGet(() -> {
                              userService.saveUser(user);
                              return new ResponseEntity<>(user, HttpStatus.CREATED);
                          });
    }

    //------------------- Update a User --------------------------------------------------------

    @RequestMapping(value = "{name}", method = RequestMethod.PUT)
    public ResponseEntity<User> updateUser(@PathVariable("name") String name, @RequestBody User user) {
        logger.debug("Updating User " + name);

        return userService.findByName(name).map(userFound -> {
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    //------------------- Delete a User --------------------------------------------------------

    @RequestMapping(value = "{name}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable("name") String name) {
        logger.debug("Fetching & Deleting User with name " + name);

        return userService.findByName(name).map(userFound -> {
            userService.deleteUserByName(name);
            return new ResponseEntity<>(userFound, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    //------------------- Delete All Users --------------------------------------------------------

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteAllUsers() {
        logger.debug("Deleting All Users");
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
