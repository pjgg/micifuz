package com.micifuz.commons.handlers;

import io.vertx.ext.web.RoutingContext;

public class Redirections {
    public static void toSwaggerUI(RoutingContext rc) {
        rc.response().putHeader("Location", "/swagger-ui").setStatusCode(301).end();
    }
}
