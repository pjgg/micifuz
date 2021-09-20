package com.micifuz.authn;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.micifuz.commons.Runner;

import io.restassured.RestAssured;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class SimpleTest {

    final static String AUTHN_HOST = "localhost";
    final static int AUTHN_PORT = 8080;
    static String deploymentId;

    @BeforeAll
    static void beforeAll(Vertx vertx, VertxTestContext testContext) {
        Runner.start(vertx, AuthMainVerticle.class.getName())
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
                   .port(AUTHN_PORT)
                   .when().get("/hello")
                   .then()
                   .statusCode(200)
                   .body("hello", is("world: authN"));
    }

    @Test
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void should_healthCheck_up(Vertx vertx, VertxTestContext testContext) {
        HttpClient client = vertx.createHttpClient();

        client.request(HttpMethod.GET, AUTHN_PORT, AUTHN_HOST, "/health").compose(req -> req.send()
                                                                                            .onComplete(testContext.succeeding(httpResp -> testContext.verify(() -> {
                                                                                                assertThat(httpResp.statusCode(), is(200));
                                                                                                testContext.completeNow();
                                                                                            }))));
    }
}
