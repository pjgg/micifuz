package com.micifuz.authn;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.micifuz.authn.handlers.CheckBasicAuthHandler;
import com.micifuz.authn.handlers.FailureHandler;
import com.micifuz.authn.handlers.HelloHandler;
import com.micifuz.authn.handlers.OauthHandler;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

@ApplicationScoped
public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class);

    private static final String HELLO = "/hello";
    private static final String OAUTH = "/internal/oauth/token";
    private static final String ROOT = "/";

    @ConfigProperty(name = "app.name")
    String serviceName;

    @Inject
    FailureHandler failureHandler;

    @Inject
    HelloHandler helloHandler;

    @Inject
    CheckBasicAuthHandler checkBasicAuthHandler;

    @Inject
    OauthHandler oauthHandler;

    @Inject
    Vertx vertx;

    Router router;

    void init(@Observes Router router) {
        this.router = router;
    }

    void onStart(@Observes StartupEvent ev) {
        LOG.info(String.format("Application %s starting...", serviceName));
        SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
        addRoute(HttpMethod.GET, HELLO, rc -> helloHandler.execute(rc));
        addRoute(HttpMethod.POST, OAUTH, sessionHandler, rc -> checkBasicAuthHandler.execute(rc),
                rc -> oauthHandler.createAccessToken(rc));
        addRoute(HttpMethod.GET, ROOT, rc -> helloHandler.execute(rc));
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info(String.format("Application %s stopping...", serviceName));
    }

    private void addRoute(HttpMethod method, String path, Handler<RoutingContext>... handlers) {
        Route route = defaultRoute(method, path);
        addBodyHandler(route, method);

        for (int i = 0; i < handlers.length; i++) {
            route.handler(handlers[i]);
        }

        route.failureHandler(rc -> failureHandler.handler(rc));
    }

    private Route defaultRoute(HttpMethod method, String path) {
        return this.router.route(method, path)
                .handler(CorsHandler.create("*"))
                .handler(LoggerHandler.create());
    }

    private void addBodyHandler(Route route, HttpMethod method) {
        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PATCH)) {
            route.handler(BodyHandler.create());
        }
    }
}
