package com.micifuz.authn.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.micifuz.authn.model.Users;
import com.micifuz.authn.services.KeycloakService;

import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class UsersHandler {

    public static final String USER_ADMIN_ROLE = "admin";
    public static final String CLIENT_ID = "clientId";

    @Inject
    private KeycloakService keycloakService;

    public void createUser(RoutingContext ctx) {
        var users = ctx.getBodyAsJson().mapTo(Users.class);
        var clientId = ctx.user().<String> get(CLIENT_ID);
        var id = users.createUser(keycloakService, clientId);
        ctx.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(HttpStatus.SC_CREATED)
                .end(String.format("{\"ID\":\"%s\"}", id));
    }

    public void retrieveUser(RoutingContext ctx) {
        var userName = ctx.request().getParam("username");
        var clientId = ctx.user().<String> get(CLIENT_ID);
        var user = Users.findUserByUserName(keycloakService, clientId, userName);
        ctx.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(HttpStatus.SC_OK)
                .end(user.toJsonEncoded());
    }
}
