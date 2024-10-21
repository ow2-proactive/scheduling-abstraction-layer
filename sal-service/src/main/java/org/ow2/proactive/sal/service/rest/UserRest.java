/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package org.ow2.proactive.sal.service.rest;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ow2.proactive.sal.model.User;
import org.ow2.proactive.sal.service.service.UserService;
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
