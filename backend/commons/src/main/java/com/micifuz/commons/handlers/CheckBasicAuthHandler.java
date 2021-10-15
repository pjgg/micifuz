package com.micifuz.commons.handlers;

import com.micifuz.commons.handlers.impl.CheckBasicAuthHandlerImpl;

import io.smallrye.mutiny.vertx.MutinyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@VertxGen
@MutinyGen(com.micifuz.commons.handlers.CheckBasicAuthHandler.class)
public interface CheckBasicAuthHandler extends Handler<RoutingContext> {
    String CLIENT_ID = "client_id";
    String SECRET = "client_secret";

    String AUTHORIZATION = "Authorization";
    String BASIC = "Basic";

    static CheckBasicAuthHandler create() {
        return new CheckBasicAuthHandlerImpl();
    }
}
