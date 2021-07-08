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
        // TODO: Would it make sense to abandon AuthenticationWrapper
        //  and just map Authentication -> OAuth2AccessTokenResponse ?
        AuthenticationWrapper authenticationWrapper = new AuthenticationWrapper((Authentication) response.getAccessToken(), response.getAdditionalParameters());
        System.out.println("New AuthenticationWrapper:\n" + authenticationWrapper.toString());
        authenticationWrappers.put((Authentication) response.getAccessToken(), authenticationWrapper);
        return response;
    }
}
