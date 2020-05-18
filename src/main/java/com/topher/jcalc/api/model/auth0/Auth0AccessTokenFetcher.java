package com.topher.jcalc.api.model.auth0;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.topher.jcalc.api.configuration.auth0.Auth0Properties;
import com.topher.jcalc.api.exceptions.InternalServerErrorException;
import com.topher.jcalc.api.exceptions.UnauthorizedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Auth0AccessTokenFetcher {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String V2_OAUTH_TOKEN_ENDPOINT = "oauth/token";

    private final Auth0Properties auth0Properties;

    public Auth0AccessTokenFetcher(final Auth0Properties auth0Properties) {
        this.auth0Properties = auth0Properties;
    }

    public Jwt getJwtFromAuth0() {
        final HttpClient httpClient = HttpClient.newBuilder().build();

        final ObjectNode postBody = OBJECT_MAPPER.createObjectNode();
        postBody.set("client_id", new TextNode(auth0Properties.getClientId()));
        postBody.set("client_secret", new TextNode(auth0Properties.getClientSecret()));
        postBody.set("audience", new TextNode(auth0Properties.getAudience()));
        postBody.set("grant_type", new TextNode("client_credentials"));
        postBody.set("scop", new TextNode("read:users"));

        final HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postBody.toString()))
                .uri(URI.create(auth0Properties.getIssuer() + V2_OAUTH_TOKEN_ENDPOINT))
                .header("Content-Type", "application/json")
                .build();

        return getJwt(httpClient, request);
    }

    public Jwt getTestJwtWithUsernameAndPassword(String username, String password) {
        final HttpClient httpClient = HttpClient.newBuilder().build();

        final ObjectNode postBody = OBJECT_MAPPER.createObjectNode();
        postBody.set("client_id", new TextNode(auth0Properties.getClientId()));
        postBody.set("client_secret", new TextNode(auth0Properties.getClientSecret()));
        postBody.set("client_id", new TextNode(auth0Properties.getClientId()));
        postBody.set("audience", new TextNode(auth0Properties.getAudience()));
        postBody.set("grant_type", new TextNode("password"));
        postBody.set("username", new TextNode(username));
        postBody.set("password", new TextNode(password));

        final HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postBody.toString()))
                .uri(URI.create(auth0Properties.getIssuer() + V2_OAUTH_TOKEN_ENDPOINT))
                .header("Content-Type", "x-www-form-urlencoded")
                .build();

        return getJwt(httpClient, request);
    }

    private Jwt getJwt(HttpClient httpClient, HttpRequest request) {
        try {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                handleErrors(response);
            }

            final JsonNode responseJson = OBJECT_MAPPER.readTree(response.body());
            return new Jwt(responseJson.get("access_token").asText(), responseJson.get("expires_in").asInt());
        } catch (final IOException | InterruptedException e) {
            throw new InternalServerErrorException("Encountered unknown error when fetching JWT from Auth0.");
        }
    }

    private static void handleErrors(final HttpResponse<String> response) {
        if (response.statusCode() == 401 ) {
            throw new UnauthorizedException(response.body());
        }
        throw new InternalServerErrorException("Received unhandled Auth0 Exception when authenticating token: " + response.body());
    }
}