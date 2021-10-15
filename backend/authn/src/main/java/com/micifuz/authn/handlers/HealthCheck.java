package com.micifuz.authn.handlers;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;

import io.smallrye.health.api.AsyncHealthCheck;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.client.predicate.ResponsePredicate;

public abstract class HealthCheck implements AsyncHealthCheck {
    private static final int RETRIES = 3;
    private static final int TIMEOUT = 30;

    @ConfigProperty(name = "auth-server-url")
    String oauthServerUrl;
    String keycloakUrl;

    @Inject
    Vertx vertx;

    protected WebClient httpClient;

    @PostConstruct
    void initialize() throws URISyntaxException {
        httpClient = WebClient.create(vertx);
        URI serverUri = new URI(oauthServerUrl);
        keycloakUrl = String.format("%s://%s:%d", serverUri.getScheme(), serverUri.getHost(), serverUri.getPort());
    }

    protected Uni<HealthCheckResponse> isUp(String name) {
        try {
            return httpClient.getAbs(keycloakUrl)
                    .expect(ResponsePredicate.status(HttpURLConnection.HTTP_OK))
                    .send()
                    .map(resp -> HealthCheckResponse.builder().name(name).up().build())
                    .ifNoItem().after(Duration.ofSeconds(TIMEOUT)).fail()
                    .onFailure().retry().atMost(RETRIES);

        } catch (Exception e) {
            return Uni.createFrom().item(HealthCheckResponse.down(e.getMessage()));
        }
    }

    public abstract Uni<HealthCheckResponse> call();
}
