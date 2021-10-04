package com.micifuz.authn;

import static com.micifuz.tests.resources.containers.KeycloakTestContainer.PARAM_REALM_FILE_NAME_KEY;
import static com.micifuz.tests.resources.containers.KeycloakTestContainer.PARAM_REALM_NAME_KEY;
import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.micifuz.tests.resources.containers.KeycloakTestContainer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = KeycloakTestContainer.class, initArgs = {
        @ResourceArg(value = "keycloak-example-realm.json", name = PARAM_REALM_FILE_NAME_KEY),
        @ResourceArg(value = "micifuz", name = PARAM_REALM_NAME_KEY)
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
}
