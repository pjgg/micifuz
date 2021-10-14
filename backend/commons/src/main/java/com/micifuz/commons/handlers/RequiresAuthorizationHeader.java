package com.micifuz.commons.handlers;

import com.micifuz.commons.handlers.impl.RequiresAuthorizationHeaderImpl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface RequiresAuthorizationHeader extends Handler<RoutingContext> {

    String AUTHORIZATION = "Authorization";
    String BEARER = "bearer";

    static RequiresAuthorizationHeader create() {
        return new RequiresAuthorizationHeaderImpl();
    }
}
