package com.micifuz.authn.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotAuthorizedException;

import org.apache.commons.lang3.StringUtils;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.security.UnauthorizedException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

@ApplicationScoped
public class FailureHandler {

    public void handler(final RoutingContext ctx) {
        JsonObject error = defaultError(ctx.normalizedPath());
        securityExceptionsHandler(ctx.failure(), error);

        if (ctx.failure()instanceof HttpException ex) {
            error.put("status", ex.getStatusCode());
            error.put("error", ex.getPayload());
        }

        if (StringUtils.isBlank(error.getString("message"))) {
            error.put("message", ctx.failure().getMessage());
        }

        ctx.response().setStatusCode(error.getInteger("status"));
        ctx.response().end(error.encode());
    }

    private void securityExceptionsHandler(final Throwable failure, JsonObject error) {
        if (failure instanceof UnauthorizedException || failure instanceof NotAuthorizedException) {
            error.put("status", HttpResponseStatus.UNAUTHORIZED.code());
            error.put("error", HttpResponseStatus.valueOf(HttpResponseStatus.UNAUTHORIZED.code()).reasonPhrase());
        }
    }

    private JsonObject defaultError(String path) {
        return new JsonObject()
                .put("timestamp", System.currentTimeMillis())
                .put("status", HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                .put("error", HttpResponseStatus.valueOf(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).reasonPhrase())
                .put("path", path);
    }
}
