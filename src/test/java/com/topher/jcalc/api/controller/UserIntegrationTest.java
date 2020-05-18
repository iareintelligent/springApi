package com.topher.jcalc.api.controller;

import com.auth0.spring.security.api.authentication.AuthenticationJsonWebToken;
import com.topher.jcalc.api.db.entities.User;
import com.topher.jcalc.api.db.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private AuthenticationJsonWebToken testJwt;

    @Test
    @Order(1)
    public void createTestUser() {
        final String uuid = UUID.randomUUID().toString();
        final String auth0Id = "test|" + uuid;

        this.testUser = new User();
        this.testUser.setEmail(uuid + "@integrationTest.com");
        this.testUser.setAuth0UserId(auth0Id);
        this.testJwt = buildJwt(testUser.getAuth0UserId());

        ResponseEntity<User> response = userController.createTestUser(testUser, testJwt);
        this.testUser.setId(response.getBody().getId());

        assertEquals(auth0Id, response.getBody().getAuth0UserId());
    }

    @Test
    @Order(2)
    public void getTestUser() {
        ResponseEntity<User> maybeUser = userController.get(this.testJwt);
        assertEquals(testUser.getAuth0UserId(), maybeUser.getBody().getAuth0UserId());
        assertEquals(testUser.getEmail(), maybeUser.getBody().getEmail());
    }

    @Test
    @Order(3)
    public void updateUser() {
        testUser.setFirstName("Not Topher");
        testUser.setLastName("Not Sikorra");
        testUser.setProfilePictureUrl("//placekitten.com/300/300");

        final ResponseEntity<User> response = userController.updateUser(testUser.getId(), testUser, buildJwt(testUser.getAuth0UserId()));
        assertEquals("Not Topher", response.getBody().getFirstName());
        assertEquals("Not Sikorra", response.getBody().getLastName());
        assertEquals("//placekitten.com/300/300", response.getBody().getProfilePictureUrl());

        testUser.setFirstName("Topher");
        testUser.setLastName("Sikorra");
        testUser.setProfilePictureUrl("//placekitten.com/500/200");
        final ResponseEntity<User> response2 = userController.updateUser(testUser.getId(), testUser, buildJwt(testUser.getAuth0UserId()));
        assertEquals("Topher", response2.getBody().getFirstName());
        assertEquals("Sikorra", response2.getBody().getLastName());
        assertEquals("//placekitten.com/500/200", response2.getBody().getProfilePictureUrl());
    }
}
