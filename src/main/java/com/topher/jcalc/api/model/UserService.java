package com.topher.jcalc.api.model;

import com.auth0.spring.security.api.authentication.AuthenticationJsonWebToken;
import com.topher.jcalc.api.db.entities.User;
import com.topher.jcalc.api.db.repository.UserRepository;
import com.topher.jcalc.api.exceptions.ConflictException;
import com.topher.jcalc.api.exceptions.UnauthorizedException;
import com.topher.jcalc.api.exceptions.UnprocessableEntityException;
import com.topher.jcalc.api.model.auth0.Auth0ManagementSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final Auth0ManagementSdk auth0ManagementSdk;

    public UserService(final UserRepository userRepository, final Auth0ManagementSdk auth0ManagementSdk) {
        this.userRepository = userRepository;
        this.auth0ManagementSdk = auth0ManagementSdk;
    }

    public List<User> getAll(final AuthenticationJsonWebToken jwt) {
        assertIsAdmin(jwt);
        return userRepository.findAll();
    }

    public User getUserByJwt(final AuthenticationJsonWebToken jwt) {
        final String auth0UserId = jwt.getName();
        if (isBlank(auth0UserId)) {
            throw new UnprocessableEntityException("User ID must not be blank");
        }

        final Optional<User> maybeUser = userRepository.findByAuth0UserId(auth0UserId);
        if (maybeUser.isEmpty()) {
            return registerFromAuth0(jwt);
        }

        return maybeUser.get();
    }

    public User registerFromAuth0(final AuthenticationJsonWebToken jwt) {
        final String auth0UserId = jwt.getName();
        if (isBlank(auth0UserId)) {
            throw new UnprocessableEntityException("User id must not be blank");
        }

        final Optional<User> existingUser = userRepository.findByAuth0UserId(auth0UserId);
        if (existingUser.isPresent()) {
            throw new ConflictException("User with ID already exists in database");
        }

        final com.topher.jcalc.api.model.auth0.User auth0User = auth0ManagementSdk.get(auth0UserId);
        LOGGER.info("Fetching auth0 user from Auth0 for id: [{}], User: [{}]", auth0UserId, auth0User);

        if (auth0User == null) {
            throw new UnauthorizedException("Auth0 User id does not correspond to an Auth0 user");
        }

        //Assert existing email address isn't taken by another auth0Id
        if (auth0User.getEmail().isPresent()) {
            final Optional<User> maybeEmailTwin = userRepository.findByEmail(auth0User.getEmail().get());
            if (maybeEmailTwin.isPresent()) {
                final User emailTwin = maybeEmailTwin.get();
                if (!auth0UserId.equals(emailTwin.getAuth0UserId())) {
                    throw new ConflictException("User with email " + auth0User.getEmail().get() + " linked to an existing account");
                }
            }
        }

        final User newUserFromJwt = new User();
        newUserFromJwt.setAuth0UserId(auth0UserId);
        newUserFromJwt.setEmail((auth0User.getEmail().get()));
        if (auth0User.getEmail().isPresent()) {
            newUserFromJwt.setEmail(auth0User.getEmail().get());
        }
        if (auth0User.getFirstName().isPresent()) {
            newUserFromJwt.setFirstName(auth0User.getFirstName().get());
        }
        if (auth0User.getLastName().isPresent()) {
            newUserFromJwt.setLastName(auth0User.getLastName().get());
        }
        if (auth0User.getProfilePictureUrl().isPresent()) {
            newUserFromJwt.setProfilePictureUrl(auth0User.getProfilePictureUrl().get());
        }

        newUserFromJwt.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));

        return userRepository.save(newUserFromJwt);
    }
    
    public User updateUser(final long id, final User request, final AuthenticationJsonWebToken jwt) {
        assertUser(id, jwt);
        if (isBlank(request.getAuth0UserId())) {
            throw new UnprocessableEntityException("User Id is required.");
        }

        final Optional<User> maybeUser = userRepository.findByAuth0UserId(request.getAuth0UserId());

        if (maybeUser.isEmpty()) {
            throw new UnprocessableEntityException(String.format("We don't know any user id %s around here.", request.getId()));
        }
        final User userFromDb = maybeUser.get();

        if (!userFromDb.getAuth0UserId().equals(jwt.getName())) {
            LOGGER.warn("User [{}] is being updated by jwt user [{}]", userFromDb.getAuth0UserId(), jwt.getName());
        }

        if (id != userFromDb.getId()) {
            LOGGER.warn("User [{}] is being updated from id [{}]", userFromDb.getId(), id);
        }

        if (isNotBlank(request.getEmail())) {
            final Optional<User> emailTwin = userRepository.findByEmail(request.getEmail());
            if (emailTwin.isPresent() && !emailTwin.get().getId().equals(userFromDb.getId())) {
                throw new UnprocessableEntityException("That email address is already in use");
            }
            userFromDb.setEmail(request.getEmail());
        }

        if (isNotBlank(request.getFirstName())) {
            userFromDb.setFirstName(request.getFirstName());
        }

        if (isNotBlank(request.getLastName())) {
            userFromDb.setLastName(request.getLastName());
        }

        if (isNotBlank(request.getProfilePictureUrl())) {
            userFromDb.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        return userRepository.save(userFromDb);
    }

    public void assertIsAdmin(final AuthenticationJsonWebToken jwt) {
        if (!isAdmin(jwt)) {
            throw new UnauthorizedException("Must be admin");
        }
    }

    public void assertUser(final long userId, final AuthenticationJsonWebToken jwt) {
        final User user = getUserByJwt(jwt);
        if (user.getId() != userId && !isAdmin(jwt)) {
            throw new UnauthorizedException("Must be admin to screw around with other users' stuff");
        }
    }

    public boolean isAdmin(final AuthenticationJsonWebToken jwt) {
        final User user = getUserByJwt(jwt);
        return user.getId() == 28;
    }

    public User createTestUser(User testUser, AuthenticationJsonWebToken jwt) {
        assertIsAdmin(jwt);
        if (!testUser.getEmail().contains("@integrationTest.com")) {
            throw new UnprocessableEntityException("Nope.");
        }
        return userRepository.save(testUser);
    }

}
