package com.example.host;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.r3.conclave.common.EnclaveInstanceInfo;
import com.r3.conclave.host.EnclaveHost;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringController {
    @Autowired
    private EnclaveHost enclave;

    @GetMapping("/user")
    public String user(@CurrentSecurityContext SecurityContext securityContext) {
        System.out.println("Current security context authentication details:");
        System.out.println(securityContext.getAuthentication().getDetails());
        System.out.println();

        System.out.println("Current security context authentication principal:");
        System.out.println(securityContext.getAuthentication().getPrincipal());
        System.out.println();

        System.out.println("Current security context authentication credentials:");
        // Returns an empty string
        System.out.println(securityContext.getAuthentication().getCredentials());
        System.out.println();
        return securityContext.getAuthentication().toString();
    }

    @GetMapping("/attestation")
    public String attestation() {
        EnclaveInstanceInfo attestation = enclave.getEnclaveInstanceInfo();
        //byte[] attestationBytes = attestation.serialize();
        //return attestation.toString();
        /*
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(attestation);
        } catch (JsonProcessingException e) {
            return e.toString();
        }
         */
        return attestation.serialize().toString();
    }
}

