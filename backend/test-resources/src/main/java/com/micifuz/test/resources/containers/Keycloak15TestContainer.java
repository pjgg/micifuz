package com.micifuz.test.resources.containers;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class Keycloak15TestContainer implements QuarkusTestResourceLifecycleManager {

    public static final String PARAM_REALM_PATH = "realm_path";
    public static final String PARAM_REALM_NAMES = "realm_names";

    private static final String PETSHOP_AUTH_URL_PROPERTY = "auth-server-url";
    private static final String VETS_AUTH_URL_PROPERTY = "vets.auth-server-url";
    private static final String SHELTERS_AUTH_URL_PROPERTY = "shelters.auth-server-url";
    private static final String ADMIN_AUTH_URL_PROPERTY = "admin.auth-server-url";

    private static final String USER = "admin";
    private static final String PASSWORD = "admin";
    private static final String REALM = "micifuz";
    private static final int PORT = 8080;

    private static final String DEFAULT_REALM_PATH = "/tmp/keycloak";
    private static final String REALM_PATH = "/tmp/keycloak";
    private static final String KEYCLOAK_IMAGE = System.getProperty("keycloak.15.image");

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
                .withEnv("JAVA_OPTS_APPEND", """
                        -Dkeycloak.migration.action=import
                        -Dkeycloak.migration.provider=dir
                        -Dkeycloak.migration.dir=/tmp/keycloak
                        -Dkeycloak.migration.strategy=OVERWRITE_EXISTING
                        """)
                .withClasspathResourceMapping(params.getOrDefault(PARAM_REALM_PATH, DEFAULT_REALM_PATH), REALM_PATH,
                        BindMode.READ_ONLY)
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Admin console listening.*\\s")
                        .withStartupTimeout(Duration.ofMinutes(5)));
        container.addExposedPort(PORT);
        container.start();

        Map<String, String> realmUrls = authUrl();
        Map<String, String> properties = new HashMap<>();
        properties.put(PETSHOP_AUTH_URL_PROPERTY, realmUrls.get("petshop"));
        properties.put(VETS_AUTH_URL_PROPERTY, realmUrls.get("vets"));
        properties.put(SHELTERS_AUTH_URL_PROPERTY, realmUrls.get("shelters"));
        properties.put(ADMIN_AUTH_URL_PROPERTY, realmUrls.get("master"));

        return properties;
    }

    @Override
    public void stop() {
        Optional.ofNullable(container).ifPresent(GenericContainer::stop);
    }

    private String keycloakUrl() {
        return String.format("http://localhost:%s/auth", container.getMappedPort(PORT));
    }

    private Map<String, String> authUrl() {
        Map<String, String> realmsUrls = new HashMap<>();
        var realmsNames = params.get(PARAM_REALM_NAMES).split(",");
        for (var realName : realmsNames) {
            realmsUrls.put(realName.trim(), String.format("%s/realms/%s", keycloakUrl(), realName.trim()));
        }

        return realmsUrls;
    }
}
