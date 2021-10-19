package com.micifuz.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.LoggerHandler;

public class RouteHelper {

    private static final Logger LOG = Logger.getLogger(RouteHelper.class);

    public static class Builder {
        private HttpMethod method;
        private String path;
        private JwtAuthOptions jwtAuthOptions;
        private List<Handler<RoutingContext>> handlers;
        private Handler<RoutingContext> failureHandler;

        public Builder(HttpMethod method, String path) {
            this.method = method;
            this.path = path;
            this.handlers = new ArrayList<>();
        }

        public Builder withHandlers(List<Handler<RoutingContext>> handlers) {
            this.handlers = handlers;
            return this;
        }

        public Builder withJwtAuthOptions(JwtAuthOptions jwtAuthOptions) {
            this.jwtAuthOptions = jwtAuthOptions;
            return this;
        }

        public Builder withHandler(Handler<RoutingContext> handler) {
            this.handlers.add(handler);
            return this;
        }

        public Builder withFailureHandler(Handler<RoutingContext> failureHandler) {
            this.failureHandler = failureHandler;
            return this;
        }

        public RouteHelper build() {
            var route = new RouteHelper();
            route.method = this.method;
            route.path = this.path;
            route.jwtAuthOptions = this.jwtAuthOptions;
            route.handlers = this.handlers;
            route.failureHandler = this.failureHandler;

            return route;
        }
    }

    private HttpMethod method;
    private String path;
    private JwtAuthOptions jwtAuthOptions;
    private List<Handler<RoutingContext>> handlers;
    private Handler<RoutingContext> failureHandler;

    private RouteHelper() {
    }

    public CompletionStage<Route> addToRouter(Vertx vertx, Router router) {
        var route = defaultRoute(router, method, path);
        if (jwtAuthOptions != null) {
            var jwtAuthHandler = createJWTAuthHandler(vertx, jwtAuthOptions, jwtAuthOptions.getPublicKeys());
            route = route.handler(jwtAuthHandler);
        }

        addBodyHandler(route, method);
        addHandlerToRoute(route).failureHandler(failureHandler);
        return Future.succeededFuture(route).toCompletionStage();
    }

    private Route addHandlerToRoute(Route route) {
        handlers.forEach(route::handler);
        return route;
    }

    private Route defaultRoute(Router router, HttpMethod method, String path) {
        return router.route(method, path)
                .handler(CorsHandler.create("*"))
                .handler(LoggerHandler.create());
    }

    private JWTAuthHandler createJWTAuthHandler(Vertx vertx, JwtAuthOptions jwtAuthOptions, List<Object> jwks) {
        var JWTAuthOptions = new JWTAuthOptions();
        JWTAuthOptions.setJwks(toJson(jwks, filterByUse("enc")));
        JWTAuthOptions.setJWTOptions(jwtAuthOptions.getJwtOpt());

        var jwtAuth = JWTAuth.create(vertx, JWTAuthOptions);
        var jwtAuthHandler = JWTAuthHandler.create(jwtAuth)
                .scopeDelimiter(jwtAuthOptions.getScopeDelimiter());

        if (!jwtAuthOptions.getExpectedScopes().isEmpty()) {
            jwtAuthHandler.withScopes(jwtAuthOptions.getExpectedScopes());
        }

        return jwtAuthHandler;
    }

    private List<JsonObject> toJson(List<Object> jwks, Predicate<JsonObject> filter) {
        return jwks.stream().map(JsonObject::mapFrom)
                .filter(filter)
                .collect(Collectors.toList());
    }

    static Predicate<JsonObject> filterByUse(String use) {
        return obj -> !obj.getString("use").equals(use);
    }

    static public BiConsumer<Route, Throwable> routeDeploymentInfo(HttpMethod method, String path) {
        return (str, err) -> {
            if (Objects.isNull(err)) {
                LOG.info("👍 " + String.format("%s %s", method, path));
            } else {
                LOG.error("💥 " + String.format("%s %s", method, path));
            }
        };
    }

    private void addBodyHandler(Route route, HttpMethod method) {
        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PATCH)) {
            route.handler(BodyHandler.create());
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Handler<RoutingContext>> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler<RoutingContext>> handlers) {
        this.handlers = handlers;
    }

    public Handler<RoutingContext> getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(Handler<RoutingContext> failureHandler) {
        this.failureHandler = failureHandler;
    }
}
