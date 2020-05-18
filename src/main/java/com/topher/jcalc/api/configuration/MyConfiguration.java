package com.topher.jcalc.api.configuration;

import com.topher.jcalc.api.configuration.auth0.AllAuth0Properties;
import com.topher.jcalc.api.db.repository.*;
import com.topher.jcalc.api.model.*;
import com.topher.jcalc.api.model.auth0.Auth0ManagementSdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class MyConfiguration {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AllAuth0Properties allAuth0Properties;

    @Bean
    public UserService userService() {
        return new UserService(userRepository, auth0ManagementSdk());
    }

    @Bean
    public Auth0ManagementSdk auth0ManagementSdk() {
        return new Auth0ManagementSdk(allAuth0Properties.getManagementApi());
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
