package com.gutmox.handlers;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class HealthHandler {

    public void execute(RoutingContext context) {
        context.response().putHeader("content-type", "application/json")
                .end(new JsonObject().put("status", "Show must go on").encode());
    }
}
