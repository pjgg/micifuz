package com.micifuz.authn.handlers;

import static com.micifuz.commons.handlers.impl.CheckBasicAuthHandlerImpl.CLIENT_ID;
import static com.micifuz.commons.handlers.impl.CheckBasicAuthHandlerImpl.SECRET;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.micifuz.authn.model.Credentials;
import com.micifuz.authn.utils.RealmUtils;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

@ApplicationScoped
public class OauthHandler {

    @Inject
    RealmUtils realmUtils;

    public void createAccessToken(RoutingContext ctx) {
        Session session = ctx.session();
        String clientId = session.get(CLIENT_ID);
        String secret = session.get(SECRET);
        Credentials credentials = new Credentials(realmUtils.getOauthServerURL(clientId), clientId, secret, ctx.request());
        ctx.response().putHeader("content-type", "application/json").end(Json.encode(credentials.createAccessToken()));
    }
}
