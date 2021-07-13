package com.example.host;

import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Hashtable;

/**
 * Not used , should be safe to delete
 */
@Deprecated
public class CustomOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient {

    private OAuth2AccessTokenResponseClient defaultAuthorizationCodeTokenResponseClient;

    public CustomOAuth2AccessTokenResponseClient() {
        this.defaultAuthorizationCodeTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse
            (AbstractOAuth2AuthorizationGrantRequest authorizationGrantRequest) {
        OAuth2AccessTokenResponse response = defaultAuthorizationCodeTokenResponseClient.getTokenResponse(authorizationGrantRequest);
        // TODO: Would it make sense to abandon AuthenticationWrapper
        //  and just map Authentication -> OAuth2AccessTokenResponse ?
        // ERROR: cannot map OAuth2AccessToken to Authentication
        /*
        AuthenticationWrapper authenticationWrapper = new AuthenticationWrapper((Authentication) response.getAccessToken(), response.getAdditionalParameters());
        System.out.println("New AuthenticationWrapper:\n" + authenticationWrapper.toString());
        authenticationWrappers.put((Authentication) response.getAccessToken(), authenticationWrapper);
         */
        System.out.println("Token response:\n" + response.toString());
        System.out.println("Access token:\n" + response.getAccessToken().toString());
        System.out.println("Additional parameters:\n" + response.getAdditionalParameters().toString());
        System.out.println("id_token:\n" + response.getAdditionalParameters().get("id_token").toString());
        System.out.println("id_token type:\n" + response.getAdditionalParameters().get("id_token").getClass());
        //System.out.println("Additional parameters decoded:\n" + Base64.getDecoder().decode(
                //((String) response.getAdditionalParameters().get("id_token")).getBytes()).toString());

        String[] idTokenChunks = ((String) response.getAdditionalParameters().get("id_token")).split("\\.");
        System.out.println("Header value:\n" + new String (Base64.getDecoder().decode(idTokenChunks[0])));
        System.out.println("Body value:\n" + new String (Base64.getDecoder().decode(idTokenChunks[1])));
        //System.out.println("Signature value:\n" + new String (Base64.getDecoder().decode(idTokenChunks[2])));
        //System.out.println("Additional parameters decoded:\n" + Arrays.toString(Base64.getDecoder()
                //.decode((String) response.getAdditionalParameters().get("id_token"))));
        return response;
    }
}
