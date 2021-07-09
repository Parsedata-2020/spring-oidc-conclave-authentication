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
        /*
        if (req != null) {
            req = customizeAuthorizationRequest(req, pub_key);
            System.out.println(req);
        }
         */
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        String pub_key = request.getParameter("pubkey");
        defaultResolver.setAuthorizationRequestCustomizer(pubkeyInNonce(pub_key));
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        /*
        System.out.println("resolving request with custom resolver");
        if (req != null) {
            req = customizeAuthorizationRequest(req, pub_key);
            System.out.println(req);
        }
         */
        return req;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> pubkeyInNonce(String pub_key) {
        return customizer ->
                customizer.additionalParameters(params -> params.put("nonce", pub_key));
    }
}

    /*
    // TODO: make pubkey be passed as nonce, not state
    //  ACTUALLY: state remains in the code, nonce does not
    //  Also, state is transmitted into the id token later,
    //  so it can be retrieved directly.
    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest req, String pub_key
    ) {
        // Practice nonce=B4BxlJ3qwLh5HWdnaYjZdapMcRHq94ycSCut0gHWVIo
        System.out.println(req.getAdditionalParameters());
        Map<String, Object> additionalParameters = new HashMap<String, Object>();
        additionalParameters.putAll(req.getAdditionalParameters());
        //additionalParameters.put("nonce", "B4BxlJ3qwLh5HWdnaYjZdapMcRHq94ycSCut0gHWVIo");
        additionalParameters.replace("nonce", "B4BxlJ3qwLh5HWdnaYjZdapMcRHq94ycSCut0gHWVIo");
        //return OAuth2AuthorizationRequest.from(req).additionalParameters(additionalParameters).build();
        return OAuth2AuthorizationRequest.from(req)
                .state(pub_key)
                .additionalParameters(additionalParameters)
                .build();
        //return req;
    }

     */
