package com.example.host;

import com.r3.conclave.shaded.kotlin.text.Charsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomRequestResolverTests {
    /*
    sample pubkey:
    secret

    sample nonce (sha256 of "secret"):
    K7gNU3sdo-OL0wNhqoVWhr3g6s1xYv72ol_pe_Unols
    */

    private CustomizationRequestResolver resolver;
    private ClientRegistrationRepository repository;
    private String uri;
    OAuth2AuthorizationRequest req;

    @BeforeEach
    public void setUp() {
        // DOES NOT WORK!!
        repository = mock(ClientRegistrationRepository.class);
        ClientRegistration mockRegistration = mock(ClientRegistration.class);
        when(mockRegistration.getRegistrationId()).thenReturn("google");
        when(mockRegistration.getClientAuthenticationMethod()).thenReturn(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
        when(mockRegistration.getAuthorizationGrantType()).thenReturn(AuthorizationGrantType.AUTHORIZATION_CODE);
        when(mockRegistration.getScopes()).thenReturn(Collections.singleton(OidcScopes.OPENID));
        /*
        ClientRegistration mockRegistration = (new ClientRegistration.Builder())
                .registrationId("google")
                .clientId("900783094046-ofbmg9vjjj3s7rlothspmhar185g72il.apps.googleusercontent.com")
                .clientName("")
                .clientSecret("")
                .build();
         */
        when(repository.findByRegistrationId("google")).thenReturn(mockRegistration);
        //uri = mock(String.class);
        uri = "/oauth2/authorization";
        resolver = new CustomizationRequestResolver(repository, uri);
        //String requestUri = "/oauth2/authorization/900783094046-ofbmg9vjjj3s7rlothspmhar185g72il";
        String requestUri = "/oauth2/authorization/google";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", requestUri);
        mockRequest.setServletPath(requestUri);
        mockRequest.addParameter("pubkey", "secret");
        req = resolver.resolve(mockRequest);
    }

    @Test
    public void testInsertsPublicKeyHashToNonce() {
        assert(req.getAdditionalParameters().get("nonce").equals("K7gNU3sdo-OL0wNhqoVWhr3g6s1xYv72ol_pe_Unols"));
    }

    @Test public void testInsertsPublicKeyToRequestAttribute() {
        assert(req.getAttribute("nonce").equals("secret"));
    }
}
