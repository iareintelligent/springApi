package com.topher.jcalc.api.helpers;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class HttpUtils {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final HttpHeaders headers = new HttpHeaders();

    public final <T> ResponseEntity<T> get(final String uri, final Class<T> responseClass) {
        final HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        final ResponseEntity<T> response = restTemplate.exchange(createURLWithPort(uri), HttpMethod.GET, entity, responseClass);

        return response;
    }

    private String createURLWithPort(final String uri) {
        return "http://localhost:" + port + uri;
    }
}
