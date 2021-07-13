package com.example.host;

import com.r3.conclave.common.EnclaveInstanceInfo;
import com.r3.conclave.host.EnclaveHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
public class SpringController {

    // TODO: replace this with constructor injection
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

        System.out.println("Principal type: " + securityContext.getAuthentication().getPrincipal().getClass());
        System.out.println();

        System.out.println("Oidc Token:\n" + ((DefaultOidcUser)
                securityContext.getAuthentication().getPrincipal()).getIdToken().getTokenValue()
        );

        System.out.println("Oidc Token claims:\n"
                + String.join(", ", ((DefaultOidcUser) securityContext.getAuthentication().getPrincipal())
                    .getIdToken().getClaims().keySet())
        );


        /*
        // Doesn't work; OidcIdTokenValidator doesn't take OidcIdToken
        (new OidcIdTokenValidator(registration)).validate(
                ((DefaultOidcUser) securityContext.getAuthentication().getPrincipal())
                        .getIdToken()
        )
         */
        return securityContext.getAuthentication().toString();
    }

    @GetMapping("/user/message")
    public byte[] message(@CurrentSecurityContext SecurityContext securityContext, String message) {
        /*
        RequestWrapper requestWrapper = new RequestWrapper(
                ((DefaultOidcUser) securityContext.getAuthentication().getPrincipal())
                        .getIdToken().getTokenValue(),
                message);
         */
        //return enclave.callEnclave(SerializationUtils.serialize(requestWrapper));
        enclave.deliverMail(1, message.getBytes(StandardCharsets.UTF_8),
                ((DefaultOidcUser) securityContext.getAuthentication().getPrincipal())
                        .getIdToken().getTokenValue());
        // TODO: return a page that has asyncronous javascript to get the resulting mail
        //  when it becomes available
        return null;
    }

    @GetMapping("/attestation")
    public byte[] attestation() {
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
        return attestation.serialize();
    }
}

