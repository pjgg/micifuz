package com.micifuz.commons.reponses;

import io.vertx.ext.web.RoutingContext;

public class Redirections {
    public static void toSwaggerUI(RoutingContext rc) {
        rc.response().putHeader("Location", "/swagger-ui").setStatusCode(301).end();
    }

    public static void toSwaggerUI(io.vertx.reactivex.ext.web.RoutingContext rc) {
        rc.response().putHeader("Location", "/swagger-ui").setStatusCode(301).end();
    }

    public static void toSwaggerUI(io.vertx.mutiny.ext.web.RoutingContext rc) {
        rc.response().putHeader("Location", "/swagger-ui").setStatusCode(301).end();
    }
}
