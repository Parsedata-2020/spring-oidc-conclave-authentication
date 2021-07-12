package com.example.enclave;

import com.r3.conclave.enclave.Enclave;
import org.apache.commons.lang3.SerializationUtils;

import java.nio.charset.StandardCharsets;

public class VerifierEnclave extends Enclave {
    @Override
    public byte[] receiveFromUntrustedHost(byte[] bytes) {
        RequestWrapper requestWrapper = (RequestWrapper) SerializationUtils.deserialize(bytes);
        return requestWrapper.message.getBytes(StandardCharsets.UTF_8);
    }
}
