package com.example.enclave;

import java.io.Serializable;

public class RequestWrapper implements Serializable {
    public final String token;
    public final String message;

    public RequestWrapper(String token, String message) {
        this.token = token;
        this.message = message;
    }
}
