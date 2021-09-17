package com.micifuz.shelters.handlers;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class HelloHandler {

    public void execute(RoutingContext context) {
        context.response().putHeader("content-type", "application/json")
                .end(new JsonObject().put("hello", "world: shelters").encode());
    }
}
