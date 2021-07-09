package com.example.host;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CustomizationRequestResolver
        implements OAuth2AuthorizationRequestResolver {

    private DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomizationRequestResolver(
            ClientRegistrationRepository repo,
            String authorizationRequestBaseUri
    ) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                repo,
                authorizationRequestBaseUri
        );

        //System.out.println("NEW CustomizationRequestResolver CREATED!!!");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String pub_key = request.getParameter("pubkey");
        defaultResolver.setAuthorizationRequestCustomizer(pubkeyInNonce(pub_key));
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        System.out.println("resolving request with custom resolver");
        //System.out.println("additional parameters: " + req.getAdditionalParameters());
        if (req != null) {
            System.out.println("additional parameters: " + req.getAdditionalParameters());
        }
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        String pub_key = request.getParameter("pubkey");
        defaultResolver.setAuthorizationRequestCustomizer(pubkeyInNonce(pub_key));
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        return req;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> pubkeyInNonce(String pub_key) {
        return customizer ->
                customizer.additionalParameters(params -> params.put("nonce", pub_key));
    }
}
