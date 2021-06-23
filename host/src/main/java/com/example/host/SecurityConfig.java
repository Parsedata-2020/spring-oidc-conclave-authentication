package com.example.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    ClientRegistrationRepository registrationRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestResolver(
                        new CustomizationRequestResolver(
                                registrationRepository,
                                "/oauth2/authorization"
                        )
                );
    }
}
