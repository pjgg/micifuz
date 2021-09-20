package com.micifuz.shelters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.micifuz.commons.Runner;
import com.micifuz.tests.resources.FreePortLocator;

import io.restassured.RestAssured;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class SimpleTest {
    final static String SHELTERS_HOST = "localhost";
    static int sheltersPort;
    static String deploymentId;

    @BeforeAll
    static void beforeAll(Vertx vertx, VertxTestContext testContext) throws IOException {
        sheltersPort = FreePortLocator.getFreePort();
        JsonObject scenarioConfig = new JsonObject()
                .put("server.port", sheltersPort)
                .put("path", "simple.yaml");

        Runner.start(vertx, new DeploymentOptions().setConfig(scenarioConfig), SheltersMainVerticle.class.getName())
                .onFailure(Throwable::printStackTrace)
                .onComplete(res -> {
                    deploymentId = res.result();
                    testContext.completeNow();
                });
    }

    @AfterAll
    static void afterAll(Vertx vertx) {
        vertx.undeploy(deploymentId);
    }

    @Test
    void should_simplyWork() {
        RestAssured.given()
                .port(sheltersPort)
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body("hello", is("world: shelters"));
    }

    @Test
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void should_healthCheck_up(Vertx vertx, VertxTestContext testContext) {
        HttpClient client = vertx.createHttpClient();

        client.request(HttpMethod.GET, sheltersPort, SHELTERS_HOST, "/health").compose(req -> req.send()
                .onComplete(testContext.succeeding(httpResp -> testContext.verify(() -> {
                    assertThat(httpResp.statusCode(), is(200));
                    testContext.completeNow();
                }))));
    }
}
