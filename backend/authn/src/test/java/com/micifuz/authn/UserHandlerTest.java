package com.micifuz.authn;

import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_FILE_NAME_KEY;
import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_NAME_KEY;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.micifuz.authn.model.Users;
import com.micifuz.tests.resources.containers.Keycloak15TestContainer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
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
    static final String VETS_CLIENT_ID = "vets-client-id";
    static final String VETS_SECRET = "topSecret";
    static final String PETSHOP_USER_NAME = "Pablo";
    static final String VETS_USER_NAME = "David";
    static final String SHELTERS_USER_NAME = "Sandra";
    static final String SHELTERS_CLIENT_ID = "shelters-client-id";
    static final String SHELTERS_SECRET = "topSecret";

    RequestSpecification reqSpec;
    ValidatableResponse resp;
    Users userReqPost;

    @Test
    public void getPetShopUser() {
        givenAnAccessToken(generateServiceValidAccessToken(PETSHOP_CLIENT_ID, PETSHOP_SECRET));
        whenMakeRequestTo("/internal/user/" + PETSHOP_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_OK);
        thenCheckUserNameIs(PETSHOP_USER_NAME.toLowerCase());
    }

    @Test
    public void getVetsUser() {
        givenAnAccessToken(generateServiceValidAccessToken(VETS_CLIENT_ID, VETS_SECRET));
        whenMakeRequestTo("/internal/user/" + VETS_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_OK);
        thenCheckUserNameIs(VETS_USER_NAME.toLowerCase());
    }

    @Test
    public void getSheltersUser() {
        givenAnAccessToken(generateServiceValidAccessToken(SHELTERS_CLIENT_ID, SHELTERS_SECRET));
        whenMakeRequestTo("/internal/user/" + SHELTERS_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_OK);
        thenCheckUserNameIs(SHELTERS_USER_NAME.toLowerCase());
    }

    @Test
    public void createPetShopUser() {
        givenAnAccessToken(generateServiceValidAccessToken(PETSHOP_CLIENT_ID, PETSHOP_SECRET));
        AndUser(new Users(true, "Pepito", "Pepito", "Grillo", "pepito.grillo@gmail.com"));
        whenMakeRequestTo(Method.POST, "/internal/user/");
        thenStatusCodeIs(HttpStatus.SC_CREATED);
        thenCheckIdNotNull();
    }

    @Test
    public void createVetsUser() {
        givenAnAccessToken(generateServiceValidAccessToken(VETS_CLIENT_ID, VETS_SECRET));
        AndUser(new Users(true, "VetsPepito", "VetsPepito", "Grillo", "vets.pepito.grillo@gmail.com"));
        whenMakeRequestTo(Method.POST, "/internal/user/");
        thenStatusCodeIs(HttpStatus.SC_CREATED);
        thenCheckIdNotNull();
    }

    @Test
    public void createSheltersUser() {
        givenAnAccessToken(generateServiceValidAccessToken(SHELTERS_CLIENT_ID, SHELTERS_SECRET));
        AndUser(new Users(true, "sheltersPepito", "sheltersPepito", "Grillo", "shelters.pepito.grillo@gmail.com"));
        whenMakeRequestTo(Method.POST, "/internal/user/");
        thenStatusCodeIs(HttpStatus.SC_CREATED);
        thenCheckIdNotNull();
    }

    @Test
    public void vetsTokenCanNotRetrievePetShopUser() {
        givenAnAccessToken(generateServiceValidAccessToken(VETS_CLIENT_ID, VETS_SECRET));
        whenMakeRequestTo("/internal/user/" + PETSHOP_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void sheltersTokenCanNotRetrievePetShopUser() {
        givenAnAccessToken(generateServiceValidAccessToken(SHELTERS_CLIENT_ID, SHELTERS_SECRET));
        whenMakeRequestTo("/internal/user/" + PETSHOP_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void PetShopTokenCanNotRetrieveVetsUser() {
        givenAnAccessToken(generateServiceValidAccessToken(PETSHOP_CLIENT_ID, PETSHOP_SECRET));
        whenMakeRequestTo("/internal/user/" + VETS_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void sheltersTokenCanNotRetrieveVetsUser() {
        givenAnAccessToken(generateServiceValidAccessToken(SHELTERS_CLIENT_ID, SHELTERS_SECRET));
        whenMakeRequestTo("/internal/user/" + VETS_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void vetsTokenCanNotRetrieveSheltersUser() {
        givenAnAccessToken(generateServiceValidAccessToken(VETS_CLIENT_ID, VETS_SECRET));
        whenMakeRequestTo("/internal/user/" + SHELTERS_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void petshopTokenCanNotRetrieveSheltersUser() {
        givenAnAccessToken(generateServiceValidAccessToken(PETSHOP_CLIENT_ID, PETSHOP_SECRET));
        whenMakeRequestTo("/internal/user/" + SHELTERS_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void expiredToken() throws IOException {
        givenAnAccessToken(getAccessTokenFromFile("expired_accesstoken.json").get("access_token"));
        whenMakeRequestTo("/internal/user/" + PETSHOP_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void invalidToken() {
        givenAnAccessToken("invalid");
        whenMakeRequestTo("/internal/user/" + PETSHOP_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void noTokenProvided() {
        givenAnAccessToken("Bearer ");
        whenMakeRequestTo("/internal/user/" + PETSHOP_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        givenAnAccessToken("");
        whenMakeRequestTo("/internal/user/" + PETSHOP_USER_NAME);
        thenStatusCodeIs(HttpStatus.SC_UNAUTHORIZED);

        given()
                .when()
                .get("/internal/user/" + PETSHOP_USER_NAME)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    private void givenAnAccessToken(String accessToken) {
        reqSpec = given().headers("Authorization", "Bearer " + accessToken,
                "Content-Type", ContentType.JSON,
                "Accept", ContentType.JSON).when();
    }

    private void AndUser(Users user) {
        userReqPost = user;
    }

    private void whenMakeRequestTo(String url) {
        whenMakeRequestTo(Method.GET, url);
    }

    private void whenMakeRequestTo(Method method, String url) {
        switch (method) {
            case GET:
            case DELETE:
                resp = reqSpec.get(url).then();
                break;

            case POST:
            case PUT:
                resp = reqSpec.body(userReqPost.toJsonEncoded()).post(url).then();
                break;
        }
    }

    private void thenStatusCodeIs(int expectedCode) {
        resp = resp.statusCode(expectedCode);
    }

    private void thenCheckUserNameIs(String expectedUserName) {
        resp = resp.body("userName", is(expectedUserName));
    }

    private void thenCheckIdNotNull() {
        resp = resp.body("ID", notNullValue());
    }

    private HashMap<String, String> getAccessTokenFromFile(String fileName) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream is = classLoader.getResource("expired_accesstoken.json").openStream();
        return new ObjectMapper().readValue(is, HashMap.class);
    }

    private String generateServiceValidAccessToken(String clientId, String secret) {
        return given()
                .when()
                .auth().preemptive().basic(clientId, secret)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .when()
                .post("/internal/oauth/token")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract().body().jsonPath().get("access_token");
    }
}
