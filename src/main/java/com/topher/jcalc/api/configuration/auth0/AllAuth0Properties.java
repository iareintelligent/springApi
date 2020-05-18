package com.topher.jcalc.api.configuration.auth0;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="auth0")
public class AllAuth0Properties {
    private Auth0Properties main;
    private Auth0Properties managementApi;

    public Auth0Properties getMain() {
        return main;
    }

    public void setMain(final Auth0Properties main) {
        this.main = main;
    }

    public Auth0Properties getManagementApi() {
        return managementApi;
    }

    public void setManagementApi(final Auth0Properties managementApi) {
        this.managementApi = managementApi;
    }
}
