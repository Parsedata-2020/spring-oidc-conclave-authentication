package com.example.enclave;

public class RequestHandler {

    public RequestHandler() {
    }

    public byte[] handleMessage(byte[] message, String userId) {
        // temporarily, just return the message that it received
        return message;
    }
}
