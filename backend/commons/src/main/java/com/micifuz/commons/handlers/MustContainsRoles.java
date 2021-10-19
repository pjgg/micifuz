package com.micifuz.commons.handlers;

import java.util.List;

import com.micifuz.commons.handlers.impl.MustContainsRolesImpl;

import io.smallrye.mutiny.vertx.MutinyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@VertxGen
@MutinyGen(com.micifuz.commons.handlers.MustContainsRoles.class)
public interface MustContainsRoles extends Handler<RoutingContext> {

    String ROLES = "roles";
    String AUTHORIZATION = "Authorization";
    String BEARER = "bearer";

    static MustContainsRoles create(List<String> expectedRoles) {
        return new MustContainsRolesImpl(expectedRoles);
    }
}
