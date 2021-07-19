package com.example.host;

import com.r3.conclave.common.EnclaveInstanceInfo;
import com.r3.conclave.common.internal.EnclaveInstanceInfoImpl;
import com.r3.conclave.common.internal.attestation.Attestation;
import com.r3.conclave.host.EnclaveHost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class ControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private EnclaveHost enclave;

    private OAuth2User principal;

    /**
     * Test that the /attestation endpoint, when hit  by an unauthenticated user,
     * causes the controller to retrieve the EnclaveInstanceInfo from the enclave
     * in the method that we expect:
     * enclave.getEnlcaveInstanceInfo()
     *
     * Ensure that the returned EnclaveInstanceInfo's serialized bytes exactly match whatever
     * attestation the enclave gives.
     *
     * @throws Exception
     */
    @Test
    public void testAttestation() throws Exception {
        // Create a new mock EnclaveInstanceInfo that, when serialized, produces a known and expected output.
        // We know we will be serializing this in the Controller.
        EnclaveInstanceInfo mockEnclaveInstanceInfo = mock(EnclaveInstanceInfo.class);
        when(mockEnclaveInstanceInfo.serialize()).thenReturn("attestation".getBytes(StandardCharsets.UTF_8));

        when(enclave.getEnclaveInstanceInfo()).thenReturn(mockEnclaveInstanceInfo);

        mvc.perform(get("/attestation"))
                .andExpect(status().isOk())
                .andExpect(content().bytes("attestation".getBytes(StandardCharsets.UTF_8)));
    }
}
