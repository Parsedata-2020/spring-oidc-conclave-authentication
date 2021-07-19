package com.example.host;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
class OidcServerApplicationTests {

    /*
	@Autowired
	private MockMvc mockMvc;
     */

    // TODO: check that proper beans are generated
    //  (is this class really worthy of a unit test?)

	// All of the below should be in integration test folder, not unit test
	@Test
	void contextLoads() {
	}

	@Test
	public void authenticatedUserSendingMail() {
		// user should successfully send mail to the enclave, need some enclave feedback?
        // user sends messages to /user/message

		// TODO: generate a proper id token
        // String id_token = "";

        final String id_token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFiZjhhODRkM2VjZDc3ZTlmMmFkNWYwNmZm" +
                "ZDI2MDcwMWRkMDZkOTAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZ" +
                "S5jb20iLCJhenAiOiI5MDA3ODMwOTQwNDYtb2ZibWc5dmpqajNzN3Jsb3Roc3BtaGFyMTg1ZzcyaWwuY" +
                "XBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI5MDA3ODMwOTQwNDYtb2ZibWc5dmpqajNzN" +
                "3Jsb3Roc3BtaGFyMTg1ZzcyaWwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTMwM" +
                "DU2MzI1MjAzOTIxMjcyOTgiLCJlbWFpbCI6ImRzaHRlaW5ib2tAZ21haWwuY29tIiwiZW1haWxfdmVya" +
                "WZpZWQiOnRydWUsImF0X2hhc2giOiI0Qy05aFRSdE1pMk1ub0pNdU9Uakx3Iiwibm9uY2UiOiJURGlYS" +
                "HVGSE4weTUwWlEtelplMHBZOVFUbjdXSHVQSVZ1Nko1c19KbWVrIiwibmFtZSI6IkRhbmllbCBTaHRla" +
                "W5ib2siLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EtL0FPaDE0R" +
                "2pXWDJRU0gtSjFLY01ZSko5U3Jrc09Bd3NxdTYwTlU4elNTSW0xPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6I" +
                "kRhbmllbCIsImZhbWlseV9uYW1lIjoiU2h0ZWluYm9rIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2MjYzN" +
                "Tg0NjYsImV4cCI6MTYyNjM2MjA2Nn0.lEKdWX6d9YrYX6fnuzzl_S-gJF_Pk3-7W3ekbJLZQJnJB8iWW" +
                "8qAV7j_tcxDmIb9h3H3qAXHmbStuuayJSwnRYEDgF8-oYhXdaYV22tiCL8MbGOLKjZaOzinSENJirrhu" +
                "BiYvvl4IrMFxHlXl46oPJWhaNbfsFUMT_q7PG6R7BjikajXM9zj1qVe4UP-DNdvXngImgnH_XBkJI5ak" +
                "wP3lEmJdzU5qD0_spTRliKN2OHWRFCxB3mEY9ctrlBX65Lb1EARrCpQ4Ke" +
                "XA4M_aEQS3mrg1GzFiVHc2lRhPaMwgkwhpn8Aj4-LuaW4j33R5tO1y2WSTQuT9MKeRyGQmTf09Q";

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
