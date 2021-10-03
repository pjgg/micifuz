package com.micifuz.tests.resources.containers;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class KeycloakTestContainer implements QuarkusTestResourceLifecycleManager {

    public static final String PARAM_REALM_FILE_NAME_KEY = "realm_file";
    public static final String PARAM_REALM_NAME_KEY = "realm_name";

    private static final String OIDC_AUTH_URL_PROPERTY = "quarkus.oidc.auth-server-url";
    private static final String OIDC_CLIENT_AUTH_URL_PROPERTY = "quarkus.oidc-client.auth-server-url";

    private static final String USER = "admin";
    private static final String PASSWORD = "admin";
    private static final String REALM = "micifuz";
    private static final int PORT = 8080;

    private static final String DEFAULT_REALM_FILE = "realm.json";
    private static final String REALM_FILE = "/tmp/" + DEFAULT_REALM_FILE;
    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:15.0.2";

    private GenericContainer<?> container;
    private Map<String, String> params = new HashMap<>();

    @Override
    public void init(Map<String, String> params) {
        this.params.putAll(params);
    }

    @SuppressWarnings("resource")
    @Override
    public Map<String, String> start() {
        container = new GenericContainer<>(KEYCLOAK_IMAGE)
                .withEnv("KEYCLOAK_USER", USER)
                .withEnv("KEYCLOAK_PASSWORD", PASSWORD)
                .withEnv("KEYCLOAK_IMPORT", REALM_FILE)
                .withClasspathResourceMapping(params.getOrDefault(PARAM_REALM_FILE_NAME_KEY, DEFAULT_REALM_FILE), REALM_FILE,
                        BindMode.READ_ONLY)
                .waitingFor(Wait.forHttp("/auth").withStartupTimeout(Duration.ofMinutes(5)));
        container.addExposedPort(PORT);
        container.start();

        Map<String, String> properties = new HashMap<>();
        properties.put(OIDC_AUTH_URL_PROPERTY, oidcAuthUrl());
        properties.put(OIDC_CLIENT_AUTH_URL_PROPERTY, oidcAuthUrl());

        return properties;
    }

    @Override
    public void stop() {
        Optional.ofNullable(container).ifPresent(GenericContainer::stop);
    }

    private String keycloakUrl() {
        return String.format("http://localhost:%s/auth", container.getMappedPort(PORT));
    }

    private String oidcAuthUrl() {
        return String.format("%s/realms/%s", keycloakUrl(), params.getOrDefault(PARAM_REALM_NAME_KEY, REALM));
    }
}
