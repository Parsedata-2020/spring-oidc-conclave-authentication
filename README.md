# OpenID Connect Via Enclave
A division of OpenID Connect authentication between an untrusted operating system, and a trusted execution environment.

## Getting started
### Preparation
Clone this repository.

Then, create the following file: `host/src/main/resources/application.yml`.

Into the `application.yml` file, paste the following:

```
spring:
  security:
    oauth2:
      client:
        provider:
          mocklab:
            authorization-uri: https://oauth.mocklab.io/oauth/authorize
            token-uri: https://oauth.mocklab.io/oauth/token
            user-info-uri: https://oauth.mocklab.io/userinfo
            user-name-attribute: sub
            jwk-set-uri: https://oauth.mocklab.io/.well-known/jwks.json
          registration:
            mock-oidc:
              provider: mocklab
              authorization-grant-type: authorization_code
              scope: openid, profile, email
              redirect-uri: "{baseUrl}/{action}/oauth2/code/{registrationId}"
              clientId: <your-client-id>
              clientSecret: <your-client-secret>
```

Here, the OpenID Connect provider is Mocklab, and in their particular case you can replace the
`<your-client-id>` and `<your-client-secret` with anything. 
If you would like to use a different OP, change the contents of this file accordingly.

### Running
#### 1. Starting
Run the Spring server with `./gradlew host:bootRun`.

#### 2. Signing in
In a browser, navigate to 
`http://localhost:8080/oauth2/authorization/mock-oidc?pubkey=a946160f377bc3591cd0224bcc38ec120f2c16ab7705ccdb3ddff372c89e7e24`

In the URL, `mock-oidc` is the registered OP from the `application.yml` file, and the value of `pubkey`
is the hex-encoded public key, corresponding to a private key that is the SHA256 hash of the byte representation
of the String "secret". 
It doesn't really matter what this `pubkey` value is, as long as it is the public key used to encrypt messages sent later.
The value of `pubkey` used here is the same that is printed to the console when running the Spring server,
printed as `getEncoded: a946160f377bc3591cd0224bcc38ec120f2c16ab7705ccdb3ddff372c89e7e24`.

The Mocklab "sign in" page that you will see is just a facade; 
you can enter whatever value you want for the email and password (or even no value), 
and the auth process will continue.

Once you sign in, you will be taken to page that says *Whitelabel Error Page*, 
which just means that there isn't an explicit page served and Spring creates this for us.

#### 3. Sending a message
After you have signed in, you can send messages to the enclave.
Open up an inspection tool in your browser and copy the value of the `JSESSIONID` cookie.
In Firefox, you can do that this way:
* Right click anywhere on the page
* In the context menu, select `Inspect`
* Navigate to the `Storage` tab in the horizontal menu
* On the left in the vertical menu, click `Cookies`
* In the list of buttons that appears below the `Cookies` tab, click `http://localhost:8080`
* Copy the value of `JSESSIONID` to your clipboard from the table in the middle of the inspection window

Now, send a GET request with the same `JSESSIONID` cookie and a `plain/text` body of

```
002800000000000000000000076d657373616765000000124820f3376ae6b2f2034d3b7a4b48a77800017190de8aadc3be261cc638233716804dfd899f3469c53d399ad3c5bc16ce5103c04938c9d0986e8f0e713fabf3c418a1688a475d4d1bd8920cec04d77a99c1f2f0df3a1aaf1a8f47f0bf015f19ba12a8b0568022c40ea852d78912018c5007f1002a1490018175f7323d53c66503352063f4dc4944e776b873cef83fa2d8ada709bf926a4fc316094e5bb50d0012b51fbf7db1dae6a5ef536b2887f2f2071a43
```

to `localhost:8080/user/message`. You can do this with Postman or Curl.

The thing that you put in the body is a Mail containing the String "test message", 
encrypted by the same key-pair whose public key you put in the `pubkey` url-encoded argument in step 2. 
This is also generated and printed to the terminal when you run the server,
printed right after `public key: Curve25519PublicKey(A946160F377BC3591CD0224BCC38EC120F2C16AB7705CCDB3DDFF372C89E7E24)`.

In Curl, the command to send that message could be:
```shell
curl -H "Content-Type: text/plain" --request GET  \
-b JSESSIONID=FB35DE9392CA488C1684A3C573BD4785 \
-d 002800000000000000000000076d657373616765000000124820f3376ae6b2f2034d3b7a4b48a77800017190de8aadc3be261cc638233716804dfd899f3469c53d399ad3c5bc16ce5103c04938c9d0986e8f0e713fabf3c418a1688a475d4d1bd8920cec04d77a99c1f2f0df3a1aaf1a8f47f0bf015f19ba12a8b0568022c40ea852d78912018c5007f1002a1490018175f7323d53c66503352063f4dc4944e776b873cef83fa2d8ada709bf926a4fc316094e5bb50d0012b51fbf7db1dae6a5ef536b2887f2f2071a43 \
localhost:8080/user/message
```
Of course, replace the value of JSESSIONID with whatever the value is from your browser.

However you send that request, you will get an empty 200 response if everything is successful.
Navigating to the terminal in which you ran the host in step 1, you will see a line `Enclave> test message`.
Currently, the enclave simply prints the message to the terminal after verifying and decrypting it.

### Abusing!
#### Replay attack
While the server is still running, repeat step 3 to send the same article of Mail again.
You will see that the enclave throws an `IllegalStateException` because it detected a replay.
Each message encrypted as mail will have an increasing counter that prevents replay attacks.
This is the same thing that would happen if a malicious host tried to replay a message from a signed-in user.

#### Mail forgery
Repeat steps 1 and 2, but this time for step 2 replace the 
`pubkey=a946160f377bc3591cd0224bcc38ec120f2c16ab7705ccdb3ddff372c89e7e24`
with `pubkey=abc123`. This represents a case where the user's public key is `abc123`.

Repeat step 3. You will see that the enclave throws another error:
`java.lang.IllegalArgumentException: Invalid public key!`.
This is because the public key hash in the OpenID Connect token doesn't match the public key with which
the Mail was encrypted. This is what occurs if a host tries to inject mail made by itself as if from a user,
or if it mixes up mail sent by different users.

The only way for Mail to be accepted by the enclave is for it to be in agreement with the nonce in the OpenID Connect
token, and the only way for that to be the case is for the user to follow the 302 redirect and sign in.
Any client-side implementation should verify the value of the nonce in the redirect matches the `pubkey`
value in the request.

## Modifying
All logic for verifying Mail based on the routingHint (the OIDC token) is contained in VerifierEnclave.
VerfierEnclave is an abstract class, and after it performs the verification it calls an unimplemented `handleMessage`
method with trusted, decrypted message bytes.

To add some functionality to the enclave, extend VerifierEnclave and implement `handleMessage` with whatever custom logic.
The motivation behind using inheritance is that it preserves access to things like `postMail` and `postOffice`.
Currently, `RequestHandler.java` is the enclave class that is being used; any replacement for it must
be reflected in `OidcServerApplication` in the `enclave` bean 
(change `String enclaveName="com.example.enclave.RequestHandler"` 
to match the fully qualified name of the enclave that you use).

Note that Conclave only allows one Enclave class per Gradle module, 
so whatever custom class you create will have to replace RequestHandler (you will have to delete it).