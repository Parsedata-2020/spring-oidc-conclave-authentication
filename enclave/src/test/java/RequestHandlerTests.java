import com.example.enclave.RequestHandler;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.r3.conclave.common.SHA256Hash;
import com.r3.conclave.enclave.EnclavePostOffice;
import com.r3.conclave.mail.Curve25519PrivateKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

public class RequestHandlerTests {
    private RequestHandler handler;

    // I need a token that just has the claim 'vids'
    // Eventually, this RequestHandler will make decisions about what to do based on the below 'vids' claim
    // Therefore, for testing, I am creating such a token here.
    // Since no verification is being performed in RequestHandler, I don't need signatures etc
    private final JWT token = new PlainJWT(new JWTClaimsSet.Builder()
            .claim("vids", new JSONArray()
                    .appendElement(
                            (new JSONObject())
                                .appendField("vid", "Batmobile")
                                .appendField("url", "localhost:10008")
                                .appendField("pubkey", new Curve25519PrivateKey(
                                        SHA256Hash.hash("secret".getBytes(StandardCharsets.UTF_8)).getBytes()
                                ))
                    )
            )
            .build()
    );

    public RequestHandlerTests() throws ParseException {
    }

    /**
     * Simply create a fresh RequestHandler before each test
     */
    @BeforeEach
    public void setup() {
        handler = new RequestHandler();
    }

    /**
     * For now, simply test deserialization and re-serialization
     * of an example array of Flow class and arguments.
     * In a Corda node, one could call ServiceHub.startFlow() passing this array as an argument
     * to start the appropriate flow.
     * Eventually, the VerifierEnclave could use the extracted url (which could be replaced with an X500 name)
     * as a routingHint to refer to the destination Corda node.
     * @throws ParseException
     */
    @Test
    public void sendSuccessfulMessage() throws ParseException {
        class EnergyTransferFlow {}
        final String url = "localhost:10080";

        Object[] message = {url, EnergyTransferFlow.class, 7};
        byte[] messageBytes = SerializationUtils.serialize(message);

        byte[] response = handler.handleMessage(messageBytes, token);

        // RequestHandler should suss out the URL and set it as the routingHint.
        // The rest of the parameters should be re-serialized and sent again.

        // the expected re-serialized message, if it is expected that the URL is pruned:
        /*
        Object[] expectedResponse = {EnergyTransferFlow.class, 7};
        byte[] expectedResponseBytes = SerializationUtils.serialize(expectedResponse);
         */

        // test that the same message is returned, reserialized to be the same as it was serialized
        assertArrayEquals(messageBytes, response);
    }
}
