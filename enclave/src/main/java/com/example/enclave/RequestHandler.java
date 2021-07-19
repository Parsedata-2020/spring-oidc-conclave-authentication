package com.example.enclave;

import com.r3.conclave.enclave.EnclavePostOffice;

public class RequestHandler {

    public RequestHandler() {
    }

    public byte[] handleMessage(byte[] message, String userId, EnclavePostOffice postOffice) {
        // temporarily, just return the message that it received
        return message;
    }
}
