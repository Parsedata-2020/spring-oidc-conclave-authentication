package com.example.host;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.r3.conclave.common.SHA256Hash;
import com.r3.conclave.host.AttestationParameters;
import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;
import com.r3.conclave.host.MailCommand;
import com.r3.conclave.mail.Curve25519PrivateKey;
import com.r3.conclave.mail.Curve25519PublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.web.servlet.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;

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
		//enclave.start(null, null);
		System.out.println("Created enclave");
		return enclave;
	}

	@Bean
	public AtomicReference<byte[]> mailToSend() throws EnclaveLoadException {
	    AtomicReference<byte[]> mailToSend = new AtomicReference<>();
	    enclave().start(new AttestationParameters.DCAP(), (commands) -> {
	    	for (MailCommand command : commands) {
	    		if (command instanceof MailCommand.PostMail) {
	    			mailToSend.set(((MailCommand.PostMail) command).getEncryptedBytes());
				}
			}
		});
	    return mailToSend;
	}

	public static void main(String[] args) {
		SpringApplication.run(OidcServerApplication.class, args);
		generateSecretKey("secret".getBytes(StandardCharsets.UTF_8));
	}

	// TODO DELETE!!!
	// Just temporary, to generate data for unit tests of enclave
	public static Curve25519PrivateKey generateSecretKey(byte[] startFrom) {
		Curve25519PrivateKey privateKey = new Curve25519PrivateKey(
				SHA256Hash.hash(startFrom).getBytes()
		);
		System.out.println("toString: " + privateKey.getPublicKey().toString());
		System.out.println("getEncoded: " + new String(Hex.encode(privateKey.getPublicKey().getEncoded())));
		// test whether you can go back from getEncoded().toString()
        // getEncoded().toString() returns a weird string that doesn't turn back into 256 bits
		if (new Curve25519PublicKey(
				Hex.decode(
						new String(Hex.encode(privateKey.getPublicKey().getEncoded()))
				)
		).equals(privateKey.getPublicKey())) {
			System.out.println("Spring Hex-encoded keys match!");
		} else {
			System.out.println("Spring Hex-encoded keys don't match!");
		}

		return privateKey;
	}



}
