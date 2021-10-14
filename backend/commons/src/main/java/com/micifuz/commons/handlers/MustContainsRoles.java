package com.micifuz.commons.handlers;

import java.util.List;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

public class MustContainsRoles implements Handler<RoutingContext> {

    private static final String ROLES = "roles";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "bearer";

    private List<String> expectedRoles;

    private MustContainsRoles(List<String> expectedRoles) {
        this.expectedRoles = expectedRoles;
    }

    @Override
    public void handle(RoutingContext ctx) {
        final String authorization = ctx.request().headers().get(AUTHORIZATION);
        if (authorization != null && authorization.toLowerCase().startsWith(BEARER.toLowerCase())) {
            var user = ctx.user();
            var roles = retrieveRoles(user);
            for (var expectedRole : expectedRoles) {
                if (!roles.contains(expectedRole)) {
                    ctx.fail(new HttpException(401, "Missing required role"));
                    break;
                }
            }
        }

        ctx.next();
    }

    private JsonArray retrieveRoles(final User user) {
        return user.principal().getJsonArray(ROLES);
    }

    static public MustContainsRoles create(List<String> expectedRoles) {
        return new MustContainsRoles(expectedRoles);
    }
}
