package com.example.host;

import org.springframework.security.core.Authentication;

import java.util.Map;

public class AuthenticationWrapper {
    private final Authentication authentication;
    private final Map<String, Object> additionalParameters;

    public AuthenticationWrapper (
            Authentication authentication,
            Map<String, Object> additionalParameters
    ) {
        this.authentication = authentication;
        this.additionalParameters = additionalParameters;
    }

    public String toString() {
        return "Authentication: " + authentication.toString()
                + "\nAdditional Parameters: " + additionalParameters.toString();
    }
}
