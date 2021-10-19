package com.micifuz.authn;

import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_NAMES;
import static com.micifuz.tests.resources.containers.Keycloak15TestContainer.PARAM_REALM_PATH;
import static io.restassured.RestAssured.given;

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
public class HealthCheckTest {
    @Test
    public void serviceUp() {
        given()
                .when()
                .get("/health")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void livenessUp() {
        given()
                .when()
                .get("/health/live")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void readinessUp() {
        given()
                .when()
                .get("/health/ready")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
