package com.example.enclave;

import com.nimbusds.jose.util.ArrayUtils;
import com.nimbusds.jwt.JWT;
import com.r3.conclave.enclave.EnclavePostOffice;
import org.springframework.util.SerializationUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RequestHandler {

    public RequestHandler() {
    }

    public byte[] handleMessage(byte[] message, JWT idToken) {
        // in the future, can query other services etc to verify that the user is allowed
        // to interact with the vehicle in question.
        // for now, not checking permissions.
        // message should be deserialized into Object[] such that the first element is the destination URL,
        // the second element is of type Class (representing the flow to initiate),
        // and the rest of the elements are arguments to pass to the flow.
        // This is how Corda flows are initiated.

        Object[] messageDeserialized = (Object[]) SerializationUtils.deserialize(message);
        String url = (String) messageDeserialized[0];

        byte[] reserialized = SerializationUtils.serialize(Arrays.copyOfRange(messageDeserialized, 1, messageDeserialized.length-1));
        System.out.println(reserialized[0]);

        //byte[] encryptedMail = postOffice.encryptMail(reserialized);
        //postOffice.encryptMail(reserialized);
        //postOffice.encryptMail("hello".getBytes(StandardCharsets.UTF_8));

        //postMail(encryptedMail);

        // returning message for now
        return message;
    }
}
