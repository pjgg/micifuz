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
    static Vertx vertx = Vertx.vertx();

    @BeforeAll
    public static void beforeAll() {
        vertx = Vertx.vertx();
        deploymentId = Main.start(vertx, new DeploymentOptions()).blockingGet();
    }

    @AfterAll
    public static void afterAll() {
        vertx.undeploy(deploymentId);
    }

    @Test
    void should_simplyWork(){
        System.out.println("Not assertions");
    }
}
