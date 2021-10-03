package com.micifuz.authn.handlers;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.client.predicate.ResponsePredicate;

@Readiness
@Liveness
@ApplicationScoped
public class HealthChecks implements AsyncHealthCheck {

    private static final int RETRIES = 3;
    private static final int TIMEOUT = 30;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String oauthServerUrl;
    String keycloakUrl;

    @Inject
    Vertx vertx;

    private WebClient httpClient;

    @PostConstruct
    void initialize() throws URISyntaxException {
        httpClient = WebClient.create(vertx);
        URI serverUri = new URI(oauthServerUrl);
        keycloakUrl = String.format("%s://%s:%d", serverUri.getScheme(), serverUri.getHost(), serverUri.getPort());
    }

    @Override
    public Uni<HealthCheckResponse> call() {
        try {
            return httpClient.getAbs(keycloakUrl)
                    .expect(ResponsePredicate.status(HttpURLConnection.HTTP_OK))
                    .send()
                    .map(resp -> HealthCheckResponse.up("Keycloak Up!"))
                    .ifNoItem().after(Duration.ofSeconds(TIMEOUT)).fail()
                    .onFailure().retry().atMost(RETRIES);

        } catch (Exception e) {
            return Uni.createFrom().item(HealthCheckResponse.down(e.getMessage()));
        }
    }
}
