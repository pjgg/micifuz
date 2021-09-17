package com.micifuz.petshop.ioc;

import com.micifuz.petshop.handlers.HealthHandler;
import com.micifuz.petshop.handlers.HelloHandler;
import com.micifuz.petshop.router.Routing;

public class IoC {

    private final Routing routing;
    private final HelloHandler helloHandler;
    private final HealthHandler healthCheck;
    private static IoC instance = null;

    public static synchronized IoC getInstance() {
        if (instance == null) {
            instance = new IoC();
        }
        return instance;
    }

    public IoC() {
        routing = new Routing();
        healthCheck = new HealthHandler();
        helloHandler = new HelloHandler();
    }

    public Routing getRouting() {
        return routing;
    }

    public HelloHandler getHelloHandler() {
        return helloHandler;
    }

    public HealthHandler getHealthCheck() {
        return healthCheck;
    }
}
