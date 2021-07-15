package com.example.enclave;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.mail.EnclaveMail;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.text.ParseException;

/**
 * The enclave proper, which only receives messages and verifies the id token sent with them.
 * Messages are forwarded to RequestHandler which handles the actual business logic,
 * and should be tested separately from VerifierEnclave.
 */
public class VerifierEnclave extends Enclave {
    // TODO use receiveMail with token in routingHint

    RequestHandler requestHandler;

    public VerifierEnclave() {
        requestHandler = new RequestHandler();
    }

    /**
     * Set another request handler.
     * Intended for use only when unit testing, allowing the use of a mock RequestHandler
     * (so as to separate testing of identity verification and actual business logic
     * to do with requests that the thing handles).
     *
     * A host will use an Enclave through EnclaveHost, which does NOT expose the underlying enclave.
     * Therefore, this method will be INACCESSIBLE outside of a testing environment.
     * More specifically, the only way to access this method is to go through MockHost instead of EnclaveHost.
     *
     * @param newHandler the new handler, which could be a mockito mock.
     */
    public void setRequestHandler(RequestHandler newHandler) {
        requestHandler = newHandler;
    }

    /**
     * Receive mail from the host, with the routingHint being the id token of the authenticated user
     * sending the message.
     * verifies that the id token's signature is valid,
     * and matches the expected IDP or OP.
     * Then, extracts some user ID (as a string) from the token,
     * or alternatively gets the user id from some other source appropriately
     * (e.g. looks up a user by Cognito ID in another external service,
     * retrieves a Corda Account name from this external service,
     * and then uses THAT Corda Account name as the user ID).
     * Regardless, it is SOME String; which one it will actually be and how it will be used
     * is to be defined in and tested with the RequestHandler.
     *
     * @param id the number representing the sequence of mails received
     * @param mail the actual signed Mail, which the host is unable to modify
     * @param routingHint the id token that the host receives from the OP (e.g. as per the corresponding unit test)
     */
    @Override
    protected void receiveMail(long id, EnclaveMail mail, String routingHint) throws IllegalArgumentException {
        // The routingHint will be the String containing the base64-encoded id token
        // token variable will store the decoded token
        JWT token = null;

        // attempt to parse the token. if it is invalid throw an error.
        try {
            token = JWTParser.parse(routingHint);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        // variable to store the name of the person,
        // or whatever is used as the unique identifying string
        String name = null;
        try {
            // currently, using the "name" claim from within the token.
            // Replace with something else if needed.
            // Could foreseeably call some other service here and then come back with the identifier
            name = (String) token.getJWTClaimsSet().getClaim("name");
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        // verify that mail's public key matches the nonce in routingHint


        // do whatever must be done, based on the RequestHandler used
        byte[] responseMessage = requestHandler.handleMessage(mail.getBodyAsBytes(), name);

        // temporarily, encrypt the message and post it back with the same routingHint
        postMail(postOffice(mail).encryptMail(responseMessage), routingHint);
    }

}
