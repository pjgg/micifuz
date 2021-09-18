package com.micifuz.authn.ioc;

import com.micifuz.authn.handlers.HelloHandler;
import com.micifuz.authn.healthChecks.Procedures;
import com.micifuz.authn.router.Routing;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;

public class IoC {

    private final Routing routing;
    private final HelloHandler helloHandler;
    private final HealthCheckHandler healthCheckHandler;
    private final Procedures healthCheckProcedures;
    private static com.micifuz.authn.ioc.IoC instance = null;

    public static synchronized com.micifuz.authn.ioc.IoC getInstance() {
        if (instance == null) {
            instance = new com.micifuz.authn.ioc.IoC();
        }
        return instance;
    }

    public IoC() {
        routing = new Routing();
        Vertx vertx = Vertx.currentContext().owner();
        healthCheckProcedures = new Procedures(vertx);
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
