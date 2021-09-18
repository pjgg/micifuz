package com.micifuz.authn;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;

public class SimpleTest {

    static String deploymentId;
    static Vertx vertx = Vertx.vertx();

    @BeforeAll
    static void beforeAll() {
        deploymentId = Main.start(vertx, new DeploymentOptions()).blockingGet();
    }

    @AfterAll
    static void afterAll() {
        vertx.undeploy(deploymentId);
    }

    @Test
    void should_simplyWork() {
        RestAssured.given()
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body("hello", is("world: authN"));
    }

    @Test
    void should_healthCheck_up() {
        RestAssured.given()
                .when().get("/health")
                .then()
                .statusCode(200);
    }
}
