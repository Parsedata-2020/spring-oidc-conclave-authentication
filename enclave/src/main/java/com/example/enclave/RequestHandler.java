package com.example.enclave;

import com.nimbusds.jwt.JWT;
import org.springframework.util.SerializationUtils;

import java.text.ParseException;

/**
 * Handles requests by deserializing the passed-in message, and doing something.
 * This is where any logical restrictions on different flows would be imposed
 * (perhaps based on the content of the id token, etc)
 */
public class RequestHandler {

    public RequestHandler() {
    }

    public byte[] handleMessage(byte[] message, JWT idToken) throws ParseException {
        // in the future, can query other services etc to verify that the user is allowed
        // to interact with the vehicle in question.
        // However, this would require storage of sessions and would slow stuff down. Bad idea!
        // for now, not checking permissions.

        // message should be deserialized into Object[] such that the first element is the destination URL,
        // the second element is of type Class (representing the flow to initiate),
        // and the rest of the elements are arguments to pass to the flow.
        // This is how Corda flows are initiated.

        Object[] messageDeserialized = (Object[]) SerializationUtils.deserialize(message);
        String url = (String) messageDeserialized[0];
        // do something with the url, or otherwise with the deserialized message

        byte[] reserialized = SerializationUtils.serialize(messageDeserialized);
        System.out.println(reserialized[0]);

        return reserialized;
    }
}
