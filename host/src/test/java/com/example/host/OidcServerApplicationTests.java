package com.example.host;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OidcServerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void authenticatedUserSendingMail() {
		// user should successfully send mail to the enclave, need some enclave feedback?
        // user sends messages to /user/message

	}

	@Test
	public void authenticatedUserPubkeyMismatch() {
		// same as above, but the Mail should be generated with a different key than in id token
	}

	@Test
	public void modifiedTokenShouldFail() {
		// e.g. host injects its own public key into the token state, should fail at enclave
		// due to the token not matching its signature
		// token header should be accessed through oauth2login().tokenEndpoint.accessTokenResponseClient()
	}

}
