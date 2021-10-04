package com.micifuz.authn;

import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_FILE_NAME_KEY;
import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_NAME_KEY;
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

@QuarkusTest
@QuarkusTestResource(value = Keycloak15TestContainer.class, initArgs = {
        @ResourceArg(value = "keycloak-example-realm.json", name = PARAM_REALM_FILE_NAME_KEY),
        @ResourceArg(value = "micifuz", name = PARAM_REALM_NAME_KEY)
})
public class ServiceAuthTest {

    static final String PETSHOP_CLIENT_ID = "petshop-client-id";
    static final String PETSHOP_SECRET = "topSecret";
    static final String USER_NAME = "Pablo";
    static final String USER_PASSWORD = "Pablo";

    @Test
    public void createServiceAccessToken() {
        given()
                .when()
                .auth().preemptive().basic(PETSHOP_CLIENT_ID, PETSHOP_SECRET)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .when()
                .post("/internal/oauth/token")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("access_token", is(not(empty())));
    }

    @Test
    public void createUserAccessToken() {
        given()
                .when()
                .auth().preemptive().basic(PETSHOP_CLIENT_ID, PETSHOP_SECRET)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("username", USER_NAME)
                .formParam("password", USER_PASSWORD)
                .when()
                .post("/internal/oauth/token")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("access_token", is(not(empty())));
    }

    @Test
    public void invalidServiceCredentials() {
        given()
                .when()
                .auth().preemptive().basic(PETSHOP_CLIENT_ID, "invalid")
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .when()
                .post("/internal/oauth/token")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void invalidUserCredentials() {
        given()
                .when()
                .auth().preemptive().basic(PETSHOP_CLIENT_ID, PETSHOP_SECRET)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("username", USER_NAME)
                .formParam("password", "invalid")
                .when()
                .post("/internal/oauth/token")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
}
