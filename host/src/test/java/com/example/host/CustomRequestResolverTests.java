package com.example.host;

import com.r3.conclave.shaded.kotlin.text.Charsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.mock;

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
        //uri = mock(String.class);
        uri = "/oauth2/authorization";
        resolver = new CustomizationRequestResolver(repository, uri);
        //String requestUri = "/oauth2/authorization/900783094046-ofbmg9vjjj3s7rlothspmhar185g72il";
        String requestUri = "/oauth2/authorization/google";
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", requestUri);
        mockRequest.setServletPath("900783094046-ofbmg9vjjj3s7rlothspmhar185g72il.apps.googleusercontent.com");
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
