package com.topher.jcalc.api.configuration;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import com.topher.jcalc.api.configuration.auth0.AllAuth0Properties;
import com.topher.jcalc.api.configuration.auth0.Auth0Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Profile(value = {"prod", "dev", "integration-tests"})
@Configuration
@EnableWebSecurity(debug = true)
public class Auth0JwtSecurity extends WebSecurityConfigurerAdapter {

    private final AllAuth0Properties allAuth0Properties;

    public Auth0JwtSecurity(final AllAuth0Properties allAuth0Properties) {
        this.allAuth0Properties = allAuth0Properties;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        final Auth0Properties mainAuth0Properties = allAuth0Properties.getMain();
        // jwt auth config
        JwtWebSecurityConfigurer
                .forRS256(mainAuth0Properties.getAudience(), mainAuth0Properties.getIssuer())
                .configure(http)
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/**").authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",  new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}