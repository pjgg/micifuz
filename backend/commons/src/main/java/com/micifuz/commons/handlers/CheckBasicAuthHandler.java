package com.micifuz.commons.handlers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

public class CheckBasicAuthHandler implements Handler<RoutingContext> {

    public static final String CLIENT_ID = "client_id";
    public static final String SECRET = "client_secret";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic";

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

    static public CheckBasicAuthHandler create() {
        return new CheckBasicAuthHandler();
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
