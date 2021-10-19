package com.micifuz.commons.handlers.impl;

import java.util.List;

import com.micifuz.commons.handlers.MustContainsRoles;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

public class MustContainsRolesImpl implements MustContainsRoles {

    private List<String> expectedRoles;

    public MustContainsRolesImpl(List<String> expectedRoles) {
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
}
