package com.example.host;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
        String pub_key = request.getParameter("pubkey");
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        System.out.println("resolving request with custom resolver");
        if (req != null) {
            req = customizeAuthorizationRequest(req, pub_key);
            System.out.println(req);
        }
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        String pub_key = request.getParameter("pubkey");
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        System.out.println("resolving request with custom resolver");
        if (req != null) {
            req = customizeAuthorizationRequest(req, pub_key);
            System.out.println(req);
        }
        return req;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest req, String pub_key
    ) {
        System.out.println(req.getAdditionalParameters());
        Map<String, Object> additionalParameters = new HashMap<String, Object>();
        additionalParameters.putAll(req.getAdditionalParameters());
        //additionalParameters.put("nonce", "TESTNONCE");
        //return OAuth2AuthorizationRequest.from(req).additionalParameters(additionalParameters).build();
        return OAuth2AuthorizationRequest.from(req)
                .state(pub_key)
                .additionalParameters(additionalParameters)
                .build();
        //return req;
    }
}
