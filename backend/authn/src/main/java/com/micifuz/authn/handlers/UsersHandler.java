package com.micifuz.authn.handlers;

import javax.enterprise.context.ApplicationScoped;

import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class UsersHandler {

    public void createUser(RoutingContext ctx) {
        ctx.user().principal();
        ctx.response().end();
    }
}
