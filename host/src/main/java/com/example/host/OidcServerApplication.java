package com.example.host;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.web.servlet.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class OidcServerApplication {

	public static void main(String[] args) {
		try {
			EnclaveHost.checkPlatformSupportsEnclaves(true);
			System.out.println("This platform supports enclaves in all three modes");
		} catch (EnclaveLoadException e) {
		    System.out.println("This platform only supports simulation");
		}
		String enclaveName = "com.example.enclave.VerifierEnclave";
		try (EnclaveHost enclave = EnclaveHost.load(enclaveName)){
		    enclave.start(null, null);
		} catch (EnclaveLoadException e) {
			e.printStackTrace();
		}
		SpringApplication.run(OidcServerApplication.class, args);
	}

	@GetMapping("/user")
	public String user(@CurrentSecurityContext SecurityContext securityContext) {
	    System.out.println("Current security context authentication details:");
	    System.out.println(securityContext.getAuthentication().getDetails());
	    System.out.println();
		System.out.println("Current security context authentication principal:");
		System.out.println(securityContext.getAuthentication().getPrincipal());
		return securityContext.getAuthentication().toString();
	}

}
