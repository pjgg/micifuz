package com.micifuz.commons.handlers.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import com.micifuz.commons.handlers.CheckBasicAuthHandler;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

public class CheckBasicAuthHandlerImpl implements CheckBasicAuthHandler {

    @Override
    public void handle(RoutingContext ctx) {
        String[] authValues = getAuthHeader(ctx);
        Session session = ctx.session();

        if (Objects.nonNull(session)) {
            session.put(CLIENT_ID, getClientId(authValues));
            session.put(SECRET, getClientSecret(authValues));
        }

        ctx.next();
    }

    private String getClientId(String[] authorizationHeaderValue) {
        return authorizationHeaderValue[0];
    }

    private String getClientSecret(String[] authorizationHeaderValue) {
        return authorizationHeaderValue[1];
    }

    private String[] getAuthHeader(RoutingContext context) {
        final String authorization = context.request().headers().get(AUTHORIZATION);
        if (authorization != null && authorization.toLowerCase().startsWith(BASIC.toLowerCase())) {
            String base64Credentials = authorization.substring(BASIC.length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            return credentials.split(":", 2);
        }

        throw new RuntimeException("missing Authorization header");
    }
}
