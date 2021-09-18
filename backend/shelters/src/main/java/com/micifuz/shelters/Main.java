package com.micifuz.shelters;

import io.netty.channel.DefaultChannelId;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    @SuppressWarnings("CheckReturnValue")
    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        start(deploymentOptions)
                .onSuccess(res -> LOGGER.info("Verticle running with id " + res.toLowerCase()))
                .onFailure(exception -> {
                    exception.printStackTrace();
                    LOGGER.error("Error starting !!!!!!!! " + exception.getMessage());
                });
    }

    public static Future<String> start(DeploymentOptions deploymentOptions) {
        DefaultChannelId.newInstance();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setEventLoopPoolSize(5);
        vertxOptions.setWorkerPoolSize(1);
        vertxOptions.setInternalBlockingPoolSize(1);
        return start(Vertx.vertx(vertxOptions), deploymentOptions).map(id -> {
            LOGGER.info("Main verticle ready and started <<<<<<<<<<<<<<<<<<<<<< " + id);
            return id;
        });
    }

    public static Future<String> start(Vertx vertx) {
        return start(vertx, new DeploymentOptions());
    }

    public static Future<String> start(Vertx vertx, DeploymentOptions deploymentOptions) {
        return vertx.deployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }
}