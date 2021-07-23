package com.example.enclave;

import com.nimbusds.jwt.JWT;
import com.r3.conclave.enclave.EnclavePostOffice;

public class RequestHandler {

    public RequestHandler() {
    }

    public byte[] handleMessage(byte[] message, JWT idToken, EnclavePostOffice postOffice) {
        // in the future, can query other services etc to verify that the user is allowed
        // to interact with the vehicle in question.
        // for now, not checking permissions.
        // message should be deserialized into Object[] such that the first element is the destination URL,
        // the second element is of type Class (representing the flow to initiate),
        // and the rest of the elements are arguments to pass to the flow.
        // This is how Corda flows are initiated.

        // returning message for now
        return message;
    }
}
