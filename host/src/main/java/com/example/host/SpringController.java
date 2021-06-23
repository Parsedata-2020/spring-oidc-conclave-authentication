package com.example.host;

import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringController {

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
}

