package com.gutmox.router;

import com.gutmox.handlers.HealthHandler;
import com.gutmox.handlers.HelloHandler;
import com.gutmox.ioc.IoC;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class Routing {
    private static final String HEALTH_CHECK = "/health";
    private static final String HELLO = "/hello";
    private static final String ROOT = "/";

    public Single<Router> createRouter() {
        Router router = Router.router(Vertx.currentContext().owner());
        router.post().handler(BodyHandler.create());
        router.get(ROOT).handler(IoC.getInstance().getHelloHandler()::execute);
        router.get(HELLO).handler(IoC.getInstance().getHelloHandler()::execute);
        router.get(HEALTH_CHECK).handler(IoC.getInstance().getHealthCheck()::execute);
        return Single.just(router);
    }
}

