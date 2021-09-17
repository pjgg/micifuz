package com.micifuz.petshop;

import com.micifuz.petshop.MainVerticle;
import io.netty.channel.DefaultChannelId;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    @SuppressWarnings("CheckReturnValue")
    public static void main(String[] args) {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        start(deploymentOptions)
                .subscribe(res -> LOGGER.info("Verticle running with id " + res.toLowerCase()),
                           error -> {
                               error.printStackTrace();
                               LOGGER.error("Error starting !!!!!!!! " + error.getMessage());
                           });
    }

    public static Single<String> start(DeploymentOptions deploymentOptions) {
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

    public static Single<String> start(Vertx vertx) {
        return start(vertx, new DeploymentOptions());
    }

    public static Single<String> start(Vertx vertx, DeploymentOptions deploymentOptions) {
        return vertx.rxDeployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }
}
