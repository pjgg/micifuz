package com.micifuz.authn;

import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_NAMES;
import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_PATH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.micifuz.tests.resources.containers.Keycloak15TestContainer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = Keycloak15TestContainer.class, initArgs = {
        @ResourceArg(value = "/realms", name = PARAM_REALM_PATH),
        @ResourceArg(value = "petshop, vets, shelters, master", name = PARAM_REALM_NAMES)
})
public class RootPathTest {

    @Test
    public void rootPathShouldRedirectToSwaggerUI() {
        given().redirects().follow(false).get("/")
                .then().statusCode(HttpStatus.SC_MOVED_PERMANENTLY)
                .and().header("Location", containsString("/swagger-ui"));

        given().get("/").then().statusCode(HttpStatus.SC_OK);
    }
}
