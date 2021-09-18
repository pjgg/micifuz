package com.micifuz.shelters.ioc;


import com.micifuz.shelters.handlers.HelloHandler;
import com.micifuz.shelters.healthChecks.Procedures;
import com.micifuz.shelters.router.Routing;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;

public class IoC {

    private final Routing routing;
    private final HelloHandler helloHandler;
    private final HealthCheckHandler healthCheckHandler;
    private static IoC instance = null;

    public static synchronized IoC getInstance() {
        if (instance == null) {
            instance = new IoC();
        }
        return instance;
    }

    public IoC() {
        routing = new Routing();
        Vertx vertx = Vertx.currentContext().owner();
        Procedures healthCheckProcedures = new Procedures(vertx);
        healthCheckHandler = healthCheckProcedures.getHealthCheckHandler();
        helloHandler = new HelloHandler();
    }

    public Routing getRouting() {
        return routing;
    }

    public HelloHandler getHelloHandler() {
        return helloHandler;
    }

    public HealthCheckHandler getHealthCheck() {
        return healthCheckHandler;
    }
}
