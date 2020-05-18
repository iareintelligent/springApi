package com.topher.jcalc.api.model.auth0;


import com.topher.jcalc.api.configuration.auth0.Auth0Properties;
import com.topher.jcalc.api.exceptions.InternalServerErrorException;
import com.topher.jcalc.api.exceptions.UnauthorizedException;
import com.topher.jcalc.api.exceptions.UnprocessableEntityException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A small SDK to interface with Auth0's Management API.
 *
 */
public class Auth0ManagementSdk {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String V2_USERS_ENDPOINT = "api/v2/users/{USER_ID}";

    private final Auth0AccessTokenFetcher auth0AccessTokenFetcher;

    private final String baseApiUrl;

    private Jwt jwt;

    public Auth0ManagementSdk(final Auth0Properties auth0Properties) {
        this.auth0AccessTokenFetcher = new Auth0AccessTokenFetcher(auth0Properties);
        baseApiUrl = auth0Properties.getIssuer();
        jwt = auth0AccessTokenFetcher.getJwtFromAuth0();
    }

    /**
     * Given an auth0 user id,
     *
     */
    public User get(final String auth0UserId) {
        if (isBlank(auth0UserId)) {
            throw new UnprocessableEntityException("Auth0 User Id must not be blank.");
        }

        if (jwt.isExpired()) {
            jwt = auth0AccessTokenFetcher.getJwtFromAuth0();
        }

        final HttpClient httpClient = HttpClient.newBuilder().build();
        final HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(baseApiUrl + V2_USERS_ENDPOINT.replace("{USER_ID}", URLEncoder.encode(auth0UserId))))
                .header("authorization", "Bearer " + jwt.getAccessToken())
                .build();

        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            final JsonNode responseJson = OBJECT_MAPPER.readTree(response.body());
            return buildAuth0User(auth0UserId, responseJson);

        } catch (final IOException | InterruptedException e) {
            throw new InternalServerErrorException("Encountered unknown error when fetching user details from Auth0.");
        }
    }

    private static User buildAuth0User(final String auth0UserId, final JsonNode responseJson) {
        if (responseJson.has("statusCode")) {
            handlePotentialErrors(responseJson);
        }
        if (auth0UserId.startsWith("google-oauth2|")) {
            return new GoogleUser(responseJson);
        }
        if (auth0UserId.startsWith("auth0|")) {
            return new Auth0User(responseJson);
        }

        throw new InternalServerErrorException("Encountered unknown Auth0 User Id Format: " + auth0UserId);
    }

    private static void handlePotentialErrors(final JsonNode responseJson) {
        final int statusCode = responseJson.get("statusCode").asInt();
        if (statusCode == 401 ) {
            throw new UnauthorizedException(responseJson.get("message").asText());
        }
        throw new InternalServerErrorException("Received unhandled Auth0 Exception: " + responseJson.get("message").asText());
    }

}
