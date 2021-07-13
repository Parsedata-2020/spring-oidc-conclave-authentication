package com.example.enclave;

import com.r3.conclave.enclave.Enclave;
import com.r3.conclave.mail.EnclaveMail;


public class VerifierEnclave extends Enclave {
    // TODO use receiveMail with token in routingHint

    @Override
    protected void receiveMail(long id, EnclaveMail mail, String routingHint) {
        // The routingHint will be the String containing the base64-encoded id token

        // TODO: decode the JWT and verify it and the mail public key from routingHint

        // after decryption, doStuff with getBodyAsBytes()
        byte[] responseMessage = doStuff(mail.getBodyAsBytes());

        // temporarily, encrypt the message and post it back with the same routingHint
        postMail(postOffice(mail).encryptMail(responseMessage), routingHint);
    }

    private byte[] doStuff(byte[] message) {
        // TODO do whatever stuff needs to be done here
        return message;
    }
}
