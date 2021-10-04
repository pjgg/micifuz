package com.micifuz.authn.handlers;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import io.smallrye.mutiny.Uni;

@Readiness
public class ReadinessHealthChecks extends HealthCheck {
    @Override
    public Uni<HealthCheckResponse> call() {
        return isUp("readiness");
    }
}
