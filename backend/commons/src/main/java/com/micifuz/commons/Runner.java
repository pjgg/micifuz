package com.micifuz.commons;

import io.netty.channel.DefaultChannelId;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Stream;

public class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class.getName());

    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        start(deploymentOptions, args)
                .forEach(deployedVerticle ->
                                 deployedVerticle.onSuccess(res -> LOGGER.info("Verticle running with id " + res.toLowerCase()))
                                                 .onFailure(exception -> {
                                                     exception.printStackTrace();
                                                     LOGGER.error("Error starting !!!!!!!! " + exception.getMessage());
                                                 }));
    }

    public static Stream<Future<String>> start(DeploymentOptions deploymentOptions, String[] verticles) {
        DefaultChannelId.newInstance();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(5);
        vertxOptions.setWorkerPoolSize(1);
        vertxOptions.setInternalBlockingPoolSize(1);
        return start(Vertx.vertx(vertxOptions), deploymentOptions, verticles).map(id -> {
            LOGGER.info("Main verticle ready and started <<<<<<<<<<<<<<<<<<<<<< " + id);
            return id;
        });
    }

    public static Stream<Future<String>> start(Vertx vertx, DeploymentOptions deploymentOptions, String[] verticles) {
        return Arrays.stream(verticles).map(verticle -> start(vertx, deploymentOptions, verticle));
    }

    public static Future<String> start(Vertx vertx, String verticle) {
        return start(vertx, new DeploymentOptions(), verticle);
    }

    public static Future<String> start(Vertx vertx, DeploymentOptions deploymentOptions, String verticle) {
        return vertx.deployVerticle(verticle, deploymentOptions);
    }
}
