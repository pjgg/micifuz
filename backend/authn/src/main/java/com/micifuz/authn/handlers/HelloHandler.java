package com.micifuz.authn.handlers;

import javax.enterprise.context.ApplicationScoped;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class HelloHandler {

    public void execute(RoutingContext context) {
        context.response().putHeader("content-type", "application/json")
                .end(new JsonObject().put("hello", "world: authN").encode());
    }
}
