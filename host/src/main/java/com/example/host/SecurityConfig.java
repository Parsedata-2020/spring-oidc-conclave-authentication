package com.example.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ClientRegistrationRepository registrationRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // require all requests to be authenticated
                .authorizeRequests(authorize -> authorize
                    .antMatchers("/user/**").authenticated())

                // this is how the oauth login will happen
                .oauth2Login()
                // configure the endpoint
                .authorizationEndpoint(
                        authorizationEndpoint ->
                                // add our own resolver; this changes parameters sent in the code as we like
                                authorizationEndpoint.authorizationRequestResolver(
                                        new CustomizationRequestResolver(
                                                registrationRepository,
                                                "/oauth2/authorization"
                                        )
                                )

                )
                .tokenEndpoint(tokenEndpointConfig ->
                        tokenEndpointConfig.accessTokenResponseClient(new CustomOAuth2AccessTokenResponseClient())
                );

    }

}
