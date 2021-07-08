# OpenID Connect Via Enclave
A simple attempt at running a Spring Boot server 
(with a resource protected by OpenID Connect authentication) inside a secure enclave.

### Getting started
Clone this repository.

Then, create the following file: `host/src/main/resources/application.yml`.

Into the `application.yml` file, paste the following:

```
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: <your-client-id>
            client-secret: <your-client-secret>
```

Remember to replace `<your-client-id>` with your actual client ID, and `<your-client-secret>`
with your actual client secret. replace `google` with whatever service you are using,
but this may require extra configuration if that service does not have a default
setup with Spring Security.

run `./gradlew host:run`, and if everything goes well, you will have a Spring server
running on `localhost:8080`. If your terminal doesn't spit out a bunch of scary errors,
try running (assuming you have curl) 
`curl -v http://localhost:8080/oauth2/authorization/google?pubkey=HELLO`.
You should see a header property in the response of the form:
`Location: https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=900783094046-ofbmg9vjjj3s7rlothspmhar185g72il.apps.googleusercontent.com&scope=openid%20profile%20email&state=HELLO&redirect_uri=http://localhost:8080/login/oauth2/code/google&nonce=K9gfEU-Cb_v4trhJ9Iv_MWfEc7zdewwrLDOZnBVNdpk`
where you will notice that the URL-encoded `state` matches the URL-encoded `pubkey` of the
curl request (namely, both are `HELLO`). 
You can try changing this from `HELLO` to something else, and convince yourself
that the `state` in the response changes accordingly.

~~Paste the value of `Location` into your browser, and you should be directed to a Google
sign in page (or a different service, according to your `application.yml` file).
After signing in, you should see a big mess of text; this is the authorization code that
Google will pass the RP (the host, in this case).~~ This doesn't work because the `state`
doesn't match your browser, so your browser will spit out an error. 
It *will* work if you paste `localhost:8080/oauth2/authorization/google?pubkey=HELLO`
directly into your browser, as your browser will remember the state of the request
that it gets i response.

### (Eventual) Use Case
A user has an AWS Cognito ID, can sign in with AWS Cognito, and wants to
(eventually, through intermediate APIs) make e.g. Corda transactions.
The objective here is to achieve the following:
* The user should only be able to perform the action when authenticated
* The Oauth2 Client (that receives and checks the id token) is *NOT*
  the same party that runs the e.g. Corda node.
* The party running the e.g. Corda node should *NOT* receive the user's identity info,
apart from what is necessary for the transaction 
  (eventually, the plan is to use corda accounts, 
  but a user may have multiple different accounts despite having one Cognito ID/login.
  In this case, the party running the Corda node would only receive the account name).
* The host running the Oauth2 Client is not necessarily trusted 
  (e.g. it could run in the cloud)

### (Eventual) Architecture
The user interacts with the untrusted host via an HTTP/HTTPS API.
The flow used is Code Authentication, with the caveat that the nonce 
sent with the id token request (along with the code) should be generated and checked
from the enclave, to prevent a malicious host from replaying old ID tokens.

When received from the authorization server, the ID token should be passed to the enclave,
where the nonce and signature are checked from trusted code.
From there, the enclave may do some extra work 
(e.g. query a database for the user's accounts, or process the user's request in some way),
and then communicate with the trusted party 
(that would be running the Corda node in our example)
using encrypted Mail.

The party running the Corda node wouldn't receive information about the user,
but could act with certainty (insofar as the authorization server is trusted)
that the user is valid.

A possible further advantage for this (beyond hiding user info) is to allow
for the user's request to be routed to the correct (e.g. Corda node-running) party,
where the user's account could be on one of many nodes. In general, it seems this separation
of authenticating and acting parties would be very useful for larger networks
with a lot of different acting parties.

## Current State of Affairs
Despite the grandiose description above, this project is just a small experiment so far.
Currently, the host and client are bare-bones, and the host program is (almost)
a copy-pasted Spring Boot project made with [Spring initializer](https://start.spring.io/#!type=gradle-project&language=java&platformVersion=2.5.1.RELEASE&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=graal-oidc&name=graal-oidc&description=OpenID%20Connect%20flow%20on%20GraalVM&packageName=com.example.graal-oidc&dependencies=native,web,oauth2-client)
containing the Web and Oauth2.

This is a newer version of [an old repo](https://github.com/DanielShteinbok/spring-in-conclave)
where the Spring server was run inside the enclave, but it was discovered that
that did not work well (for reasons discussed in that project's readme).
Instead, it was decided that the Spring server performing all the OAuth steps would
be housed in the untrusted host, and the enclave would simply perform
cryptographic verification and lookup of the user.