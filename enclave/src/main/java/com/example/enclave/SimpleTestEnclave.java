package com.example.enclave;

import com.nimbusds.jwt.JWT;

import java.util.MissingResourceException;

public class SimpleTestEnclave extends VerifierEnclave{

    @Override
    protected void handleMessage(byte[] message, JWT token) throws IllegalArgumentException, SecurityException, MissingResourceException, UnsupportedOperationException {
        System.out.println(new String(message));
        postMail(message, "unencrypted message");
    }

}
