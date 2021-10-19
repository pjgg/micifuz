package com.micifuz.authn;

import static com.micifuz.authn.handlers.UsersHandler.USER_ADMIN_ROLE;
import static com.micifuz.commons.RouteHelper.routeDeploymentInfo;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.micifuz.authn.handlers.FailureHandler;
import com.micifuz.authn.handlers.OauthHandler;
import com.micifuz.authn.handlers.UsersHandler;
import com.micifuz.authn.utils.RealmUtils;
import com.micifuz.commons.JwtAuthOptions;
import com.micifuz.commons.RouteHelper;
import com.micifuz.commons.handlers.CheckBasicAuthHandler;
import com.micifuz.commons.handlers.MustContainsRoles;
import com.micifuz.commons.handlers.RequiresAuthorizationHeader;
import com.micifuz.commons.reponses.Redirections;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

@ApplicationScoped
public class Application {

    private static final Logger LOG = Logger.getLogger(Application.class);

    private static final String ROOT = "/";
    private static final String OAUTH = "/internal/oauth/token";
    private static final String USER = "/internal/user";

    @ConfigProperty(name = "app.name")
    String serviceName;

    @Inject
    FailureHandler failureHandler;

    @Inject
    OauthHandler oauthHandler;

    @Inject
    UsersHandler usersHandler;

    @Inject
    RealmUtils realmUtils;

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

        createRoute(HttpMethod.GET, ROOT, failureHandler::handler, List.of(Redirections::toSwaggerUI));

        var sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
        var checkBasicHeader = CheckBasicAuthHandler.create();
        var oauthHandlerChain = List.of(sessionHandler, checkBasicHeader, oauthHandler::createAccessToken);
        createRoute(HttpMethod.POST, OAUTH, failureHandler::handler, oauthHandlerChain);

        var jwtAuthOpt = new JwtAuthOptions.Builder(realmUtils.getJWKS(), client).build();
        var oauthHandlersCreate = defaultOauthHandlers(List.of(USER_ADMIN_ROLE), usersHandler::createUser);
        createRoute(HttpMethod.POST, USER, failureHandler::handler, oauthHandlersCreate, jwtAuthOpt);

        var oauthHandlersRead = defaultOauthHandlers(List.of(USER_ADMIN_ROLE), usersHandler::retrieveUser);
        createRoute(HttpMethod.GET, USER + "/:username", failureHandler::handler, oauthHandlersRead, jwtAuthOpt);
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info(String.format("Application %s stopping...", serviceName));
    }

    private CompletionStage<Route> createRoute(HttpMethod method, String path, Handler<RoutingContext> failureHandler,
            List<Handler<RoutingContext>> handlers, JwtAuthOptions jwtAuthOptions) {
        return createRouterHelper(method, path, failureHandler, handlers)
                .withJwtAuthOptions(jwtAuthOptions)
                .build()
                .addToRouter(vertx, router)
                .whenComplete(routeDeploymentInfo(method, path));
    }

    private CompletionStage<Route> createRoute(HttpMethod method, String path, Handler<RoutingContext> failureHandler,
            List<Handler<RoutingContext>> handlers) {
        return createRouterHelper(method, path, failureHandler, handlers)
                .build()
                .addToRouter(vertx, router)
                .whenComplete(routeDeploymentInfo(method, path));
    }

    private RouteHelper.Builder createRouterHelper(HttpMethod method, String path, Handler<RoutingContext> failureHandler,
            List<Handler<RoutingContext>> handlers) {
        return new RouteHelper.Builder(method, path).withFailureHandler(failureHandler).withHandlers(handlers);
    }

    private List<Handler<RoutingContext>> defaultOauthHandlers(List<String> roles, Handler<RoutingContext> additionalHandler) {
        var checkOauthHeader = RequiresAuthorizationHeader.create();
        return List.of(checkOauthHeader, MustContainsRoles.create(roles), additionalHandler);
    }
}
