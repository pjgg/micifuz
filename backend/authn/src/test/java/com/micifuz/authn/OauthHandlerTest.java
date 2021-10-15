package com.micifuz.authn;

import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_NAMES;
import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_PATH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.micifuz.tests.resources.containers.Keycloak15TestContainer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@QuarkusTestResource(value = Keycloak15TestContainer.class, initArgs = {
        @ResourceArg(value = "/realms", name = PARAM_REALM_PATH),
        @ResourceArg(value = "petshop, vets, shelters, master", name = PARAM_REALM_NAMES)
})
public class OauthHandlerTest {

    static final String PETSHOP_CLIENT_ID = "petshop-client-id";
    static final String PETSHOP_SECRET = "topSecret";
    static final String USER_NAME = "Pablo";
    static final String USER_PASSWORD = "Pablo";

    RequestSpecification reqSpec;
    ValidatableResponse resp;

    @Test
    public void createServiceAccessToken() {
        givenClientIdAndSecretAndGrantType(PETSHOP_CLIENT_ID, PETSHOP_SECRET, "client_credentials");
        whenMakeQueryToTokenUrlWithUserAndPassword("/internal/oauth/token", "", "");
        thenCheckResponseStatusIs(HttpStatus.SC_OK);
        thenCheckAccessTokenNotEmpty();
    }

    @Test
    public void createUserAccessToken() {
        givenClientIdAndSecretAndGrantType(PETSHOP_CLIENT_ID, PETSHOP_SECRET, "password");
        whenMakeQueryToTokenUrlWithUserAndPassword("/internal/oauth/token", USER_NAME, USER_PASSWORD);
        thenCheckResponseStatusIs(HttpStatus.SC_OK);
        thenCheckAccessTokenNotEmpty();
    }

    @Test
    public void invalidServiceCredentials() {
        givenClientIdAndSecretAndGrantType(PETSHOP_CLIENT_ID, "invalid", "client_credentials");
        whenMakeQueryToTokenUrlWithUserAndPassword("/internal/oauth/token", "", "");
        thenCheckResponseStatusIs(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void invalidUserCredentials() {
        givenClientIdAndSecretAndGrantType(PETSHOP_CLIENT_ID, PETSHOP_SECRET, "password");
        whenMakeQueryToTokenUrlWithUserAndPassword("/internal/oauth/token", USER_NAME, "invalid");
        thenCheckResponseStatusIs(HttpStatus.SC_UNAUTHORIZED);
    }

    private void givenClientIdAndSecretAndGrantType(String clientId, String secret, String grantType) {
        reqSpec = given()
                .when()
                .auth().preemptive().basic(clientId, secret)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", grantType);
    }

    private void whenMakeQueryToTokenUrlWithUserAndPassword(String tokenUrl, String userName, String password) {
        resp = reqSpec.formParam("username", userName)
                .formParam("password", password)
                .when()
                .post(tokenUrl).then();
    }

    private void thenCheckResponseStatusIs(int expectedStatus) {
        resp.statusCode(expectedStatus);
    }

    private void thenCheckAccessTokenNotEmpty() {
        resp.body("access_token", is(not(empty())));
    }
}
