package com.topher.jcalc.api.model.auth0;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class GoogleUser implements User {

    private final JsonNode user;

    public GoogleUser(final JsonNode user) {
        this.user = user;
    }

    @Override
    public Optional<String> getId() {
        if (user.has("user_id")) {
            return Optional.of(user.get("user_id").asText());
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
        if (user.has("given_name")) {
            return Optional.of(user.get("given_name").asText());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getLastName() {
        if (user.has("family_name")) {
            return Optional.of(user.get("family_name").asText());
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getProfilePictureUrl() {
        if (user.has("picture")) {
            return Optional.of(user.get("picture").asText());
        }
        return Optional.empty();
    }
}