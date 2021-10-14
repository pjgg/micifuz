package com.micifuz.commons.handlers;

import com.micifuz.commons.handlers.impl.CheckBasicAuthHandlerImpl;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@VertxGen
public interface CheckBasicAuthHandler extends Handler<RoutingContext> {
    String CLIENT_ID = "client_id";
    String SECRET = "client_secret";

    String AUTHORIZATION = "Authorization";
    String BASIC = "Basic";

    static CheckBasicAuthHandler create() {
        return new CheckBasicAuthHandlerImpl();
    }
}
