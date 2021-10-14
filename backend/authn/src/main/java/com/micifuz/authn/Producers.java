package com.micifuz.authn;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;

public class Producers {

    @ConfigProperty(name = "quarkus.oidc.admin.auth-server-url")
    String adminOauthServerUrl;

    @ConfigProperty(name = "quarkus.oidc.admin.client-id")
    String adminClientId;

    @ConfigProperty(name = "quarkus.oidc.admin.realm")
    String adminRealm;

    @ConfigProperty(name = "quarkus.oidc.admin.userName")
    String adminUserName;

    @ConfigProperty(name = "quarkus.oidc.admin.password")
    String adminPassword;

    @Produces
    @Singleton
    Keycloak keycloakAdminCli() {
        return Keycloak.getInstance(
                StringUtils.substringBefore(adminOauthServerUrl, "/realm"),
                adminRealm,
                adminUserName, adminPassword,
                adminClientId);
    }

}
