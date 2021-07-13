package com.example.host;

import com.example.enclave.RequestWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.r3.conclave.common.EnclaveInstanceInfo;
import com.r3.conclave.host.EnclaveHost;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.web.servlet.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenValidator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.SerializationUtils;
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
        RequestWrapper requestWrapper = new RequestWrapper(
                ((DefaultOidcUser) securityContext.getAuthentication().getPrincipal())
                        .getIdToken().getTokenValue(),
                message);
        return enclave.callEnclave(SerializationUtils.serialize(requestWrapper));
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

