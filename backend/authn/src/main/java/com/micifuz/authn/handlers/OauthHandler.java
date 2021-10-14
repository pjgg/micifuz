package com.micifuz.authn.handlers;

import static com.micifuz.commons.handlers.impl.CheckBasicAuthHandlerImpl.CLIENT_ID;
import static com.micifuz.commons.handlers.impl.CheckBasicAuthHandlerImpl.SECRET;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.micifuz.authn.model.Credentials;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

@ApplicationScoped
public class OauthHandler {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String oauthServerUrl;

    public void createAccessToken(RoutingContext ctx) {
        Session session = ctx.session();
        String clientId = session.get(CLIENT_ID);
        String secret = session.get(SECRET);
        Credentials credentials = new Credentials(oauthServerUrl, clientId, secret, ctx.request());
        ctx.response().putHeader("content-type", "application/json").end(Json.encode(credentials.createAccessToken()));
    }
}
