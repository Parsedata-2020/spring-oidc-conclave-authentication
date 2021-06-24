package com.example.client;

import com.r3.conclave.common.EnclaveInstanceInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request attestationRequest = new Request.Builder()
                .url("http://localhost:8080")
                .build();
        Response attestationResponse = client.newCall(attestationRequest).execute();
        EnclaveInstanceInfo attestation
                = EnclaveInstanceInfo.deserialize(attestationResponse.body().bytes());
        System.out.println("Client received attestation!");
        System.out.println(attestation.toString());
    }
}
