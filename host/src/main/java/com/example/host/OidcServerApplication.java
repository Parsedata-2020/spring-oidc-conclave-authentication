package com.example.host;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.servlet.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Hashtable;

@SpringBootApplication
public class OidcServerApplication {

	@Bean(destroyMethod = "close")
	public EnclaveHost enclave() throws EnclaveLoadException {
		try {
			EnclaveHost.checkPlatformSupportsEnclaves(true);
			System.out.println("This platform supports enclaves in all three modes");
		} catch (EnclaveLoadException e) {
			System.out.println("This platform only supports simulation");
		}
		String enclaveName = "com.example.enclave.VerifierEnclave";
		EnclaveHost enclave = EnclaveHost.load(enclaveName);
		enclave.start(null, null);
		return enclave;
	}

	@Bean
	public Hashtable<Authentication, AuthenticationWrapper> authenticationWrappers() {
	    System.out.println("CREATED NEW authenticationWrappers HASHTABLE");
		return new Hashtable<>();
	}

	public static void main(String[] args) {
		SpringApplication.run(OidcServerApplication.class, args);
	}


}
