package com.example.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.util.Hashtable;

public class CustomOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient {

    private OAuth2AccessTokenResponseClient defaultAuthorizationCodeTokenResponseClient;

    @Autowired
    private Hashtable<Authentication, AuthenticationWrapper> authenticationWrappers;

    public CustomOAuth2AccessTokenResponseClient() {
        this.defaultAuthorizationCodeTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse
            (AbstractOAuth2AuthorizationGrantRequest authorizationGrantRequest) {
        OAuth2AccessTokenResponse response = defaultAuthorizationCodeTokenResponseClient.getTokenResponse(authorizationGrantRequest);
        return response;
    }
}
