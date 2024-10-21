/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ow2.proactive.sal.model.User;
import org.ow2.proactive.sal.service.Application;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


/**
 * Created by ActiveEon Team on 4/27/2016.
 */

@ActiveProfiles("test")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { Application.class })
@WebIntegrationTest(randomPort = true)
public class SpringUserRestTest extends AbstractRestTest {

    final RestTemplate restTemplate = new RestTemplate();

    private String REST_SERVICE_URI;

    @Before
    public void configureRestAssured() {
        REST_SERVICE_URI = "http://localhost:" + serverPort + "/users/";
    }

    //testing using springframework RestTemplate
    /* POST, PUT and GET */
    @Test
    public void testCreateUpdateGetUser() {
        User user = new User("Marco", 18, 70000);

        // create user
        ResponseEntity<User> respUser = restTemplate.postForEntity(REST_SERVICE_URI, user, User.class);
        assertThat(respUser.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(respUser.getBody(), is(user));

        //update user
        user.setAge(1);
        restTemplate.put(REST_SERVICE_URI + user.getName(), user);

        //get user
        User retrievedUser = restTemplate.getForObject(REST_SERVICE_URI + user.getName(), User.class);
        assertTrue(retrievedUser.equals(user));
    }

    /* POST, GET, DELETE */
    @Test
    public void testCreateSeveralAndDeleteUsers() {
        User user1 = new User("Marco", 18, 70000);
        User user2 = new User("Tobias", 18, 70000);
        User user3 = new User("Yaro", 18, 70000);

        // create several users
        ResponseEntity<User> respUser1 = restTemplate.postForEntity(REST_SERVICE_URI, user1, User.class);
        ResponseEntity<User> respUser2 = restTemplate.postForEntity(REST_SERVICE_URI, user2, User.class);
        ResponseEntity<User> respUser3 = restTemplate.postForEntity(REST_SERVICE_URI, user3, User.class);

        assertThat(respUser1.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(respUser2.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(respUser3.getStatusCode(), is(HttpStatus.CREATED));

        //delete user
        restTemplate.delete(REST_SERVICE_URI + user1.getName());

        //find all users
        Collection allUsers = restTemplate.getForObject(REST_SERVICE_URI, Collection.class);
        assertThat(allUsers.size(), is(2));

        //delete all users
        restTemplate.delete(REST_SERVICE_URI);

        //find all users
        Collection allEmptyUsers = restTemplate.getForObject(REST_SERVICE_URI, Collection.class);
        assertThat(allEmptyUsers, is(nullValue()));
    }
}
