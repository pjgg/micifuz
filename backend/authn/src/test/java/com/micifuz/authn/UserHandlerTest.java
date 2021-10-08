package com.micifuz.authn;

import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_FILE_NAME_KEY;
import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_NAME_KEY;
import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.micifuz.tests.resources.containers.Keycloak15TestContainer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@QuarkusTestResource(value = Keycloak15TestContainer.class, initArgs = {
        @ResourceArg(value = "keycloak-example-realm.json", name = PARAM_REALM_FILE_NAME_KEY),
        @ResourceArg(value = "micifuz", name = PARAM_REALM_NAME_KEY)
})
public class UserHandlerTest {

    static final String PETSHOP_CLIENT_ID = "petshop-client-id";
    static final String PETSHOP_SECRET = "topSecret";
    static final String USER_NAME = "Pablo";
    static final String USER_PASSWORD = "Pablo";

    RequestSpecification reqSpec;
    ValidatableResponse resp;

    @Test
    public void getPetShopUser() {
        givenAnAccessToken(generateValidAccessToken());
        whenMakeRequestTo("/user");
        thenStatusCodeIs(HttpStatus.SC_OK);
    }

    @Test
    public void expiredToken() throws IOException {
        givenAnAccessToken(getAccessTokenFromFile("expired_accesstoken.json").get("access_token"));
        whenMakeRequestTo("/user");
        thenStatusCodeIs(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void invalidToken() {
        givenAnAccessToken("invalid");
        whenMakeRequestTo("/user");
        thenStatusCodeIs(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void noTokenProvided() {
        givenAnAccessToken("Bearer ");
        whenMakeRequestTo("/user");
        thenStatusCodeIs(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        givenAnAccessToken("");
        whenMakeRequestTo("/user");
        thenStatusCodeIs(HttpStatus.SC_UNAUTHORIZED);

        given()
                .when()
                .get("/user")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    private void givenAnAccessToken(String accessToken) {
        reqSpec = given().headers("Authorization", "Bearer " + accessToken,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON).when();
    }

    private void whenMakeRequestTo(String url) {
        resp = reqSpec.get(url).then();
    }

    private void thenStatusCodeIs(int expectedCode) {
        resp = resp.statusCode(expectedCode);
    }

    private HashMap<String, String> getAccessTokenFromFile(String fileName) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream is = classLoader.getResource("expired_accesstoken.json").openStream();
        return new ObjectMapper().readValue(is, HashMap.class);
    }

    private String generateValidAccessToken() {
        return given()
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
                .extract().body().jsonPath().get("access_token");
    }
}
