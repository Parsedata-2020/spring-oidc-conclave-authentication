package com.example.host;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;

public class CustomizationRequestResolver
        implements OAuth2AuthorizationRequestResolver {

    private OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomizationRequestResolver(
            ClientRegistrationRepository repo,
            String authorizationRequestBaseUri
    ) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                repo,
                authorizationRequestBaseUri
        );
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        if (req != null) {
            req = customizeAuthorizationRequest(req);
        }
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        if (req != null) {
            req = customizeAuthorizationRequest(req);
        }
        return req;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest req
    ) {
        return OAuth2AuthorizationRequest.from(req).state("YOSKIBROSKIthisisaSTATE").build();
    }
}
