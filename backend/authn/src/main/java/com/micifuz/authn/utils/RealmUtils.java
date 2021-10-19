package com.micifuz.authn.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class RealmUtils {

    private static final List<String> PETSHOP_DEFAULT_ROLES = List.of("user-role", "user-petshop");
    private static final List<String> VETS_DEFAULT_ROLES = List.of("user-role", "user-vets");
    private static final List<String> SHELTERS_DEFAULT_ROLES = List.of("user-role", "user-shelters");
    private static final String JWKS_PATH = "/protocol/openid-connect/certs";

    private final Map<String, List<String>> defaultClientIdRoles = new HashMap<>();

    @ConfigProperty(name = "auth-server-url")
    String petshopOauthServerUrl;

    @ConfigProperty(name = "client-id")
    String petshopClientId;

    @ConfigProperty(name = "vets.auth-server-url")
    String vetsOauthServerUrl;

    @ConfigProperty(name = "vets.client-id")
    String vetsClientId;

    @ConfigProperty(name = "shelters.auth-server-url")
    String sheltersOauthServerUrl;

    @ConfigProperty(name = "shelters.client-id")
    String shelterClientId;

    @PostConstruct
    void initialize() {
        defaultClientIdRoles.put(petshopClientId, PETSHOP_DEFAULT_ROLES);
        defaultClientIdRoles.put(vetsClientId, VETS_DEFAULT_ROLES);
        defaultClientIdRoles.put(shelterClientId, SHELTERS_DEFAULT_ROLES);
    }

    public String getRealmByClientId(String clientId) {
        var serverURL = getOauthServerURL(clientId);
        return serverURL.substring(serverURL.lastIndexOf('/') + 1);
    }

    public String getOauthServerURL(String clientId) {
        String serverURL = "";
        switch (clientId) {
            case "petshop-client-id":
                serverURL = petshopOauthServerUrl;
                break;

            case "vets-client-id":
                serverURL = vetsOauthServerUrl;
                break;

            case "shelters-client-id":
                serverURL = sheltersOauthServerUrl;
                break;

            default:
                throw new RuntimeException("Unknown client ID");
        }

        return serverURL;
    }

    public List<String> getJWKS() {
        return List.of(petshopOauthServerUrl + JWKS_PATH, vetsOauthServerUrl + JWKS_PATH, sheltersOauthServerUrl + JWKS_PATH);
    }

    public Map<String, List<String>> getDefaultClientIdRoles() {
        return defaultClientIdRoles;
    }
}
