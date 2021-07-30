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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.text.ParseException;

import static org.mockito.Mockito.*;

public class RequestHandlerTests {
    private RequestHandler handler;

    /*
    private final JWT token = JWTParser.parse(
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFiZjhhODRkM2VjZDc3ZTlmMmFkNWYwNmZm" +
                    "ZDI2MDcwMWRkMDZkOTAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZ" +
                    "S5jb20iLCJhenAiOiI5MDA3ODMwOTQwNDYtb2ZibWc5dmpqajNzN3Jsb3Roc3BtaGFyMTg1ZzcyaWwuY" +
                    "XBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI5MDA3ODMwOTQwNDYtb2ZibWc5dmpqajNzN" +
                    "3Jsb3Roc3BtaGFyMTg1ZzcyaWwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTMwM" +
                    "DU2MzI1MjAzOTIxMjcyOTgiLCJlbWFpbCI6ImRzaHRlaW5ib2tAZ21haWwuY29tIiwiZW1haWxfdmVya" +
                    "WZpZWQiOnRydWUsImF0X2hhc2giOiI0Qy05aFRSdE1pMk1ub0pNdU9Uakx3Iiwibm9uY2UiOiJURGlYS" +
                    "HVGSE4weTUwWlEtelplMHBZOVFUbjdXSHVQSVZ1Nko1c19KbWVrIiwibmFtZSI6IkRhbmllbCBTaHRla" +
                    "W5ib2siLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EtL0FPaDE0R" +
                    "2pXWDJRU0gtSjFLY01ZSko5U3Jrc09Bd3NxdTYwTlU4elNTSW0xPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6I" +
                    "kRhbmllbCIsImZhbWlseV9uYW1lIjoiU2h0ZWluYm9rIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2MjYzN" +
                    "Tg0NjYsImV4cCI6MTYyNjM2MjA2Nn0.lEKdWX6d9YrYX6fnuzzl_S-gJF_Pk3-7W3ekbJLZQJnJB8iWW" +
                    "8qAV7j_tcxDmIb9h3H3qAXHmbStuuayJSwnRYEDgF8-oYhXdaYV22tiCL8MbGOLKjZaOzinSENJirrhu" +
                    "BiYvvl4IrMFxHlXl46oPJWhaNbfsFUMT_q7PG6R7BjikajXM9zj1qVe4UP-DNdvXngImgnH_XBkJI5ak" +
                    "wP3lEmJdzU5qD0_spTRliKN2OHWRFCxB3mEY9ctrlBX65Lb1EARrCpQ4Ke" +
                    "XA4M_aEQS3mrg1GzFiVHc2lRhPaMwgkwhpn8Aj4-LuaW4j33R5tO1y2WSTQuT9MKeRyGQmTf09Q"
    );
     */
    // I need a token that just has the claim 'vids'
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

    @BeforeEach
    public void setup() {
        handler = new RequestHandler();
    }

    @Test
    public void sendSuccessfulMessage() throws ParseException {
        class EnergyTransferFlow {}
        final String url = "localhost:10080";

        Object[] message = {url, EnergyTransferFlow.class, 7};
        byte[] messageBytes = SerializationUtils.serialize(message);

        //when(postOffice.encryptMail(any())).thenReturn("encrypted message".getBytes(StandardCharsets.UTF_8));

        handler.handleMessage(messageBytes, token);

        // RequestHandler should suss out the URL and set it as the routingHint.
        // The rest of the parameters should be reserialized and sent again.

        // the expected re-serialized message:
        Object[] expectedResponse = {EnergyTransferFlow.class, 7};
        byte[] expectedResponseBytes = SerializationUtils.serialize(expectedResponse);

    }
}
