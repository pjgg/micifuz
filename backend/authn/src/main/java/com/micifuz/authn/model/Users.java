package com.micifuz.authn.model;

import com.micifuz.authn.services.KeycloakService;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.Json;

@RegisterForReflection
public record Users(boolean enabled,
        String userName,
        String firstName,
        String lastName,
        String email) {

    public String createUser(final KeycloakService keycloakService, String clientId) {
        return keycloakService.createUser(this, clientId);
    }

    public static Users findUserByUserName(final KeycloakService keycloakService, final String clientId,
            final String userName) {
        return keycloakService.findUserByUserName(clientId, userName);
    }

    public String toJsonEncoded() {
        return Json.encode(this);
    }
}
