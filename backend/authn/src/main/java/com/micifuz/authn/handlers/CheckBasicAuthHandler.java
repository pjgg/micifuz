package com.micifuz.authn.handlers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.security.UnauthorizedException;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

@ApplicationScoped
public class CheckBasicAuthHandler {

    public static final String CLIENT_ID = "client_id";
    public static final String SECRET = "client_secret";

    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic";

    public void execute(RoutingContext ctx) {
        String[] authValues = getAuthHeader(ctx);
        Session session = ctx.session();
        session.put(CLIENT_ID, getClientId(authValues));
        session.put(SECRET, getClientSecret(authValues));
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

        throw new UnauthorizedException("missing Authorization header");
    }
}
