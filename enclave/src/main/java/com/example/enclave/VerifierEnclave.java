package com.example.enclave;

import com.r3.conclave.enclave.Enclave;

public class VerifierEnclave extends Enclave {
    @Override
    public byte[] receiveFromUntrustedHost(byte[] bytes) {
        return null;
    }
}
