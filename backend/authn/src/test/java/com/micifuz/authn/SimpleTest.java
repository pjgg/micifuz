package com.micifuz.authn;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;

public class SimpleTest {

    static String deploymentId;

    @BeforeAll
    public void beforeAll() {
        deploymentId = Main.start(new DeploymentOptions()).blockingGet();
    }

    @AfterAll
    public void afterAll() {
        Vertx.currentContext().owner().undeploy(deploymentId);
    }

    @Test
    void should_simplyWork(){
        System.out.println("Not assertions");
    }
}
