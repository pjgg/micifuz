package com.micifuz.commons.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

public class RequiresAuthorizationHeader implements Handler<RoutingContext> {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "bearer";

    @Override
    public void handle(RoutingContext ctx) {
        final String authorization = ctx.request().headers().get(AUTHORIZATION);
        if (authorization != null && authorization.toLowerCase().startsWith(BEARER.toLowerCase())) {
            if (authorization.substring(BEARER.length()).trim().isEmpty()) {
                ctx.fail(new HttpException(401, "Missing Authorization Bearer header"));
            }
        } else {
            ctx.fail(new HttpException(401, "Missing Authorization Bearer header"));
        }

        ctx.next();
    }

    static public RequiresAuthorizationHeader create() {
        return new RequiresAuthorizationHeader();
    }
}
