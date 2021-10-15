package com.micifuz.commons.handlers;

import com.micifuz.commons.handlers.impl.RequiresAuthorizationHeaderImpl;

import io.smallrye.mutiny.vertx.MutinyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@VertxGen
@MutinyGen(com.micifuz.commons.handlers.RequiresAuthorizationHeader.class)
public interface RequiresAuthorizationHeader extends Handler<RoutingContext> {

    String AUTHORIZATION = "Authorization";
    String BEARER = "bearer";

    static RequiresAuthorizationHeader create() {
        return new RequiresAuthorizationHeaderImpl();
    }
}
