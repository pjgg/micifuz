package com.micifuz.authn;

import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class SimpleTest {

    static String deploymentId;

    @BeforeAll
    public static void beforeAll(Vertx vertx) {
        deploymentId = Main.start(vertx).blockingGet();
    }

    @AfterAll
    public static void afterAll(Vertx vertx) {
        vertx.undeploy(deploymentId);
    }

    @Test
    void should_simplyWork(){
        System.out.println("Not assertions");
    }
}
