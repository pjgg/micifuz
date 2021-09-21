package com.micifuz.commons;

import java.util.Arrays;
import java.util.stream.Stream;

import io.netty.channel.DefaultChannelId;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class.getName());

    public static void main(String[] args) {
        System.out.println("Starting >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        start(deploymentOptions, args)
                .forEach(deployedVerticle ->
                        deployedVerticle.onSuccess(res -> LOGGER.info(">>>>>> Verticle started deployment id " + res.toLowerCase()))
                                .onFailure(exception -> {
                                    exception.printStackTrace();
                                    LOGGER.error("Error starting >>>>> " + exception.getMessage());
                                }));
    }

    public static Stream<Future<String>> start(DeploymentOptions deploymentOptions, String[] verticles) {
        return start(Vertx.vertx(getVertxOptions()), deploymentOptions, verticles);
    }

    public static Future<String> start(Vertx vertx, String verticle) {
        return start(vertx, new DeploymentOptions(), verticle);
    }

    private static VertxOptions getVertxOptions() {
        DefaultChannelId.newInstance();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(5);
        vertxOptions.setWorkerPoolSize(1);
        vertxOptions.setInternalBlockingPoolSize(1);
        return vertxOptions;
    }

    public static Stream<Future<String>> start(Vertx vertx, DeploymentOptions deploymentOptions, String[] verticles) {
        return Arrays.stream(verticles).map(verticle -> start(vertx, deploymentOptions, verticle));
    }

    public static Future<String> start(Vertx vertx, DeploymentOptions deploymentOptions, String verticle) {
        return vertx.deployVerticle(verticle, deploymentOptions);
    }
}
