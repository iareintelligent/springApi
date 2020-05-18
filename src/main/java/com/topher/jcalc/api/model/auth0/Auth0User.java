package com.topher.jcalc.api.model.auth0;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

public class Auth0User implements User {
    private JsonNode user;

    public Auth0User(final JsonNode user) { this.user = user; }

    @Override
    public Optional<String> getId() {
        if (user.has("id")) {
            return Optional.of(user.get("id").asText());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getEmail() {
        if (user.has("email")) {
            return Optional.of(user.get("email").asText());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getFirstName() {
        if (user.has("firstName")) {
            return Optional.of(user.get("firstName").asText());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getLastName() {
        if (user.has("lastName")) {
            return Optional.of(user.get("lastName").asText());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getProfilePictureUrl() {
        if (user.has("profilePictureUrl")) {
            return Optional.of(user.get("profilePictureUrl").asText());
        }
        return Optional.empty();
    }

}
