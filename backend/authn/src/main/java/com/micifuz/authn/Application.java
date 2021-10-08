package com.micifuz.authn;

import static com.micifuz.commons.RouteHelper.routeDeploymentInfo;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.micifuz.authn.handlers.FailureHandler;
import com.micifuz.authn.handlers.OauthHandler;
import com.micifuz.authn.handlers.UsersHandler;
import com.micifuz.commons.JwtAuthOptions;
import com.micifuz.commons.RouteHelper;
import com.micifuz.commons.handlers.CheckBasicAuthHandler;
import com.micifuz.commons.handlers.Redirections;
import com.micifuz.commons.handlers.RequiresAuthorizationHeader;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

@ApplicationScoped
public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class);

    private static final String ROOT = "/";
    private static final String OAUTH = "/internal/oauth/token";
    private static final String USER = "/user";
    private static final String JWKS_PATH = "/protocol/openid-connect/certs";

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String oauthServerUrl;

    @ConfigProperty(name = "app.name")
    String serviceName;

    @Inject
    FailureHandler failureHandler;

    @Inject
    OauthHandler oauthHandler;

    @Inject
    UsersHandler usersHandler;

    @Inject
    Vertx vertx;

    Router router;
    WebClient client;

    @PostConstruct
    void initialize() {
        this.client = WebClient.create(vertx);
    }

    void init(@Observes Router router) {
        this.router = router;
    }

    void onStart(@Observes StartupEvent ev) {
        LOG.info(String.format("Application %s starting...", serviceName));
        SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx.getDelegate()));

        new RouteHelper.Builder(HttpMethod.GET, ROOT)
                .withFailureHandler(failureHandler::handler)
                .withHandler(Redirections::toSwaggerUI).build()
                .addToRouter(vertx.getDelegate(), router).whenComplete(routeDeploymentInfo(HttpMethod.GET, ROOT));

        var oauthHandlerChain = List.of(BodyHandler.create(), sessionHandler, CheckBasicAuthHandler.create(),
                oauthHandler::createAccessToken);
        new RouteHelper.Builder(HttpMethod.POST, OAUTH)
                .withFailureHandler(failureHandler::handler)
                .withHandlers(oauthHandlerChain).build()
                .addToRouter(vertx.getDelegate(), router).whenComplete(routeDeploymentInfo(HttpMethod.POST, OAUTH));

        var jwtAuthOpt = new JwtAuthOptions.Builder(oauthServerUrl + JWKS_PATH, client.getDelegate())
                .build();

        new RouteHelper.Builder(HttpMethod.GET, USER)
                .withJwtAuthOptions(jwtAuthOpt)
                .withFailureHandler(failureHandler::handler)
                .withHandlers(List.of(RequiresAuthorizationHeader.create(), usersHandler::createUser)).build()
                .addToRouter(vertx.getDelegate(), router).whenComplete(routeDeploymentInfo(HttpMethod.GET, USER));
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info(String.format("Application %s stopping...", serviceName));
    }
}
