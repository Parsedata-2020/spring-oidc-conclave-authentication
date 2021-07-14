package com.example.enclave;

import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.mail.EnclaveMail;

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
    protected void receiveMail(long id, EnclaveMail mail, String routingHint) {
        // The routingHint will be the String containing the base64-encoded id token
        // TODO: decode the JWT and verify it and the mail public key from routingHint

        // TODO: get the actual userId, not everybody can be Batman ;-(
        // after decryption, doStuff with getBodyAsBytes()
        byte[] responseMessage = requestHandler.handleMessage(mail.getBodyAsBytes(), "BATMAAAAAAN!!");

        // temporarily, encrypt the message and post it back with the same routingHint
        postMail(postOffice(mail).encryptMail(responseMessage), routingHint);
    }

}
