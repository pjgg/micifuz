package com.micifuz.authn.handlers;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import io.smallrye.mutiny.Uni;

@Liveness
public class LivenessHealthChecks extends HealthCheck {
    @Override
    public Uni<HealthCheckResponse> call() {
        return isUp("liveness");
    }
}
