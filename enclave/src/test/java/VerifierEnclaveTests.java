import com.example.enclave.RequestHandler;
import com.example.enclave.VerifierEnclave;
import com.r3.conclave.host.AttestationParameters;
import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;
import com.r3.conclave.host.MailCommand;
import com.r3.conclave.testing.MockHost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.mock;

public class VerifierEnclaveTests {
    // valid OIDC token:
    // "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFiZjhhODRkM2VjZDc3ZTlmMmFkNWYwNmZmZDI2MDcwMWRkMDZkOTAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI5MDA3ODMwOTQwNDYtb2ZibWc5dmpqajNzN3Jsb3Roc3BtaGFyMTg1ZzcyaWwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI5MDA3ODMwOTQwNDYtb2ZibWc5dmpqajNzN3Jsb3Roc3BtaGFyMTg1ZzcyaWwuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTMwMDU2MzI1MjAzOTIxMjcyOTgiLCJlbWFpbCI6ImRzaHRlaW5ib2tAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJOQjdwRmZva0RCUGNIbm1JQm1OaDBRIiwibm9uY2UiOiJLN2dOVTNzZG8tT0wwd05ocW9WV2hyM2c2czF4WXY3Mm9sX3BlX1Vub2xzIiwibmFtZSI6IkRhbmllbCBTaHRlaW5ib2siLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EtL0FPaDE0R2pXWDJRU0gtSjFLY01ZSko5U3Jrc09Bd3NxdTYwTlU4elNTSW0xPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IkRhbmllbCIsImZhbWlseV9uYW1lIjoiU2h0ZWluYm9rIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2MjYyOTExMzksImV4cCI6MTYyNjI5NDczOX0.cV4E2lIt4-gQLmjBFuL8xi8QLOcLhbIKJwuTDuJ_KXHKtnQ5ENvm_qJfdzWkwk1SR2ggAAS10oAS-Ub0fJ7YYTzbNfftDoC9TYki4ERLlTdBZuojiEOMwnbwYrF683f-XXVi93xmOLioyG8YkC3wuYHuZ6kjq8ZPAzFvF-yXgUYo6xfSMjtyjCqqljJj00KjElRxV6g73dG5EtdYoy_pW_htPPYp9h30FAbenKEIePGI963ToKY4SEqNpcYgkhVkfJ98bA0bxzm9csdrvuCBq62WgzElWj_S9f4EPOFsrNWHLLxdwee08AdgMI3d7JXwPQsYSV8K-Fvvd2Q9qoVUIw"

    private RequestHandler mockHandler;
    private MockHost enclave;
    public AtomicReference<byte[]> mailToSend;

    @BeforeEach
    public void makeEnclave() throws EnclaveLoadException {
        // make sure that the platform supports enclaves
        // copied from other code, maybe should be removed from tests eventually (?)
        /*
        try {
            EnclaveHost.checkPlatformSupportsEnclaves(true);
            System.out.println("This platform supports enclaves in all three modes");
        } catch (EnclaveLoadException e) {
            System.out.println("This platform only supports simulation");
        }
         */

        // the unique identifier of the enclave class
        // unnnecessary as long as we are using MockHost.loadMock
        //final String enclaveName = "com.example.enclave.VerifierEnclave";



        // start the enclave based on the enclaveName class identifier
        enclave = MockHost.loadMock(VerifierEnclave.class);

        // unnecessary print statement
        //System.out.println("Created enclave");

        // initialize mailToSend
        mailToSend = new AtomicReference<>();

        // create a mockHandler object, which should be accessible from other tests
        // so that it is possible to verify its methods called etc
        mockHandler = mock(RequestHandler.class);



        // STARTING THE ENCLAVE

        // ensure that all mail coming out of the enclave is put into mailToSend
        // which can be accessed from all the other tests
        enclave.start(new AttestationParameters.DCAP(), (commands) -> {
            for (MailCommand command : commands) {
                if (command instanceof MailCommand.PostMail) {
                    mailToSend.set(((MailCommand.PostMail) command).getEncryptedBytes());
                }
            }
        });

        // put the mock RequestHandler as the handler that the enclave will use
        ((VerifierEnclave) enclave.getEnclave()).setRequestHandler(mockHandler);
    }

    @Test
    public void testHappyCaseReturnedMail() {
        // TODO: test the generation and delivery of mail
    }
}
