package com.micifuz.vets.healthChecks;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;

public class Procedures {

    private Map<String, Handler<Promise<Status>>> healthChecks = new HashMap<>();
    private final Vertx vertx;

    public Procedures(final Vertx vertx) {
        this.vertx = vertx;
        healthChecks.put("postgresql", postgresql());
    }

    public HealthCheckHandler getHealthCheckHandler() {
        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
        for (Map.Entry<String, Handler<Promise<Status>>> procedure : healthChecks.entrySet()) {
            healthCheckHandler.register(procedure.getKey(), procedure.getValue());
        }

        return healthCheckHandler;
    }

    public Handler<Promise<Status>> postgresql() {
        return promise -> promise.complete(Status.OK(new JsonObject().put("status", "running")));
    }
}
