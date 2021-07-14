package com.example.host;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;

import static org.mockito.Mockito.*;

/**
 * Should test that, upon CustomizationRequestResolver.resolve()
 * the setAuthorizationRequestCustomizer() is called,
 * THEN the resolve() method of the passed-in DefaultOAuth2AuthorizationRequestResolver
 * is called.
 *
 * Should also verify that the OAuth2AuthorizationRequest produced by
 * the resolve() method contains the correct attribute ("secret")
 * and the correct additional parameter ("K7gNU3sdo-OL0wNhqoVWhr3g6s1xYv72ol_pe_Unols",
 * the hash of "secret")
 *
 * NOTE: I have not gotten to testing that last part.
 * For now, I assume that DefaultOAuth2AuthorizationRequestResolver
 * works as expected, meaning that if setAuthorizationRequestCustomizer()
 * is called before resolve(), that whatever Consumer was passed in is applied.
 *
 * HOWEVER, the consumer passed in may itself be incorrect,
 * which would lead to the OAuth2AuthorizationRequest
 * fields being incorrect.
 * Since a) the method that produces this consumer is private,
 * and b) this is all sort of "implementation detail" that should not be tested,
 * I will not attempt to test this for now.
 * Furthermore, these tests should not be done in the future the way they are done now.
 * Instead, the test should just focus on input and output
 * (i.e. a known MockHttpServletRequest input and an expected output
 * with expected nonce parameter and attribute).
 * Currently, changing the internal logic (which should not be tested)
 * would cause these crappy tests to fail (as they are brittle)
 */
public class CustomRequestResolverTests {
    /*
    sample pubkey:
    secret

    sample nonce (sha256 of "secret"):
    K7gNU3sdo-OL0wNhqoVWhr3g6s1xYv72ol_pe_Unols
    */

    /**
     * Test that the resolve() method of the passed-in
     * DefaultOAuth2AuthorizationRequestResolver is called.
     */
    @Test
    public void testResolveCalled() {
        DefaultOAuth2AuthorizationRequestResolver mockResolver = mock(DefaultOAuth2AuthorizationRequestResolver.class);
        CustomizationRequestResolver customResolver = new CustomizationRequestResolver(mockResolver);

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("pubkey", "secret");

        customResolver.resolve(mockHttpServletRequest);
        verify(mockResolver).resolve(mockHttpServletRequest);
    }

    /**
     * Test that the setAuthorizationRequestCustomizer() method of the passed-in
     * DefaultOAuth2AuthorizationRequestResolver is called before
     * the resolve() method
     */
    @Test
    public void testCustomizerSet() {
        DefaultOAuth2AuthorizationRequestResolver mockResolver = mock(DefaultOAuth2AuthorizationRequestResolver.class);
        CustomizationRequestResolver customResolver = new CustomizationRequestResolver(mockResolver);

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("pubkey", "secret");

        customResolver.resolve(mockHttpServletRequest);

        // make sure stuff happens in order
        InOrder inOrder = inOrder(mockResolver);

        inOrder.verify(mockResolver).setAuthorizationRequestCustomizer(any());
        inOrder.verify(mockResolver).resolve(mockHttpServletRequest);
        inOrder.verifyNoMoreInteractions();

    }
}
