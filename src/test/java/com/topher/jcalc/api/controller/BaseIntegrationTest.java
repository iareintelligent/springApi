package com.topher.jcalc.api.controller;

import com.auth0.spring.security.api.authentication.AuthenticationJsonWebToken;
import com.topher.jcalc.api.Application;
import com.topher.jcalc.api.configuration.auth0.AllAuth0Properties;
import com.topher.jcalc.api.model.auth0.Auth0AccessTokenFetcher;
import com.topher.jcalc.api.model.auth0.Jwt;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("integration-tests")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest {



    protected static AuthenticationJsonWebToken buildJwt(final String id) {
        final AuthenticationJsonWebToken jwt = Mockito.mock(AuthenticationJsonWebToken.class);
        Mockito.when(jwt.getName()).thenReturn(id);
        return jwt;
    }

    protected Jwt getTestJwt(final String username, final String password) {
        return new Auth0AccessTokenFetcher(allAuth0Properties.getManagementApi())
        .getTestJwtWithUsernameAndPassword(username, password);
    }

    private RestTemplate patchRestTemplate;

    @Autowired
    private AllAuth0Properties allAuth0Properties;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${test.jwt}")
    private String testJwt;

    @LocalServerPort
    private int port;

    private HttpHeaders buildHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + testJwt);
        return headers;
    }

    private String createUrlWithPort(final String uri) {
        return "http://localhost:" + port + uri;
    }


    protected final <T> ResponseEntity<T> get(final String uri, final Class<T> responseClass) {
        final HttpEntity<String> entity = new HttpEntity<>(null, buildHeaders());
        final ResponseEntity<T> response = testRestTemplate.exchange(createUrlWithPort(uri), HttpMethod.GET, entity, responseClass);

        return response;
    }

    protected final <T> ResponseEntity<T> post(final String uri, final Object requestBody, final Class<T> responseClass) {
        final HttpEntity<Object> entity = new HttpEntity<>(requestBody, buildHeaders());
        final ResponseEntity<T> response = testRestTemplate.exchange(createUrlWithPort(uri), HttpMethod.POST, entity, responseClass);

        return response;
    }

    protected final <T> ResponseEntity<T> patch(final String uri, final Object requestBody, final Class<T> responseClass) {
        final HttpEntity<Object> entity = new HttpEntity<>(requestBody, buildHeaders());
        final ResponseEntity<T> response = patchRestTemplate.exchange(createUrlWithPort(uri), HttpMethod.PATCH, entity, responseClass);

        return response;
    }

    protected final <T> ResponseEntity<T> put(final String uri, final Class<T> responseClass) {
        final HttpEntity<String> entity = new HttpEntity<>(null, buildHeaders());
        final ResponseEntity<T> response = testRestTemplate.exchange(createUrlWithPort(uri), HttpMethod.PUT, entity, responseClass);

        return response;
    }

    protected final <T> ResponseEntity<T> delete(final String uri, final Class<T> responseClass) {
        final HttpEntity<String> entity = new HttpEntity<>(null, buildHeaders());
        final ResponseEntity<T> response = testRestTemplate.exchange(createUrlWithPort(uri), HttpMethod.DELETE, entity, responseClass);

        return response;
    }

    @BeforeAll
    public void init() {
        // specially build a rest template for PATCH due to HttpUrlConnection
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30 * 1000);
        requestFactory.setReadTimeout(30 * 1000);

        patchRestTemplate = testRestTemplate.getRestTemplate();

        final HttpClient httpClient = HttpClientBuilder.create().build();

        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }
}


