package com.micifuz.authn;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;

public class Producers {

    @ConfigProperty(name = "admin.auth-server-url")
    String adminOauthServerUrl;

    @ConfigProperty(name = "admin.client-id")
    String adminClientId;

    @ConfigProperty(name = "admin.realm")
    String adminRealm;

    @ConfigProperty(name = "admin.userName")
    String adminUserName;

    @ConfigProperty(name = "admin.password")
    String adminPassword;

    @Produces
    @Named("admin")
    @Singleton
    Keycloak keycloakAdminCli() {
        return Keycloak.getInstance(
                StringUtils.substringBefore(adminOauthServerUrl, "/realm"),
                adminRealm,
                adminUserName, adminPassword,
                adminClientId);
    }

}
