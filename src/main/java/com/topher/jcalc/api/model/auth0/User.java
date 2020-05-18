package com.topher.jcalc.api.model.auth0;

import java.util.Optional;

public interface User {
    Optional<String> getId();
    Optional<String> getEmail();
    Optional<String> getFirstName();
    Optional<String> getLastName();
    Optional<String> getProfilePictureUrl();
}
