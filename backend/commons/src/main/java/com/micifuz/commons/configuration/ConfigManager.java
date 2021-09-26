package com.micifuz.commons.configuration;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;

public class ConfigManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class.getName());

    private static final String ENV_SEPARATOR = "_";
    private static final String ENV_PREFIX = "X";
    private static final String DEFAULT_CONFIG_PATH = "config.yaml";
    private static ConfigManager instance = null;

    private final Single<JsonObject> config;

    private final JsonObject runtimeProperties;

    private enum FileType {JSON, YAML}

    public static synchronized ConfigManager getInstance(Vertx vertx) {
        if (instance == null) {
            instance = new ConfigManager(vertx);
        }
        return instance;
    }

    private ConfigManager(Vertx vertx) {
        runtimeProperties = vertx.getOrCreateContext().config();
        ConfigRetrieverOptions opt = new ConfigRetrieverOptions().addStore(getConfigStoreOpt(FileType.YAML));
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, opt);
        config = configRetriever.rxGetConfig();
    }

    public Single<Boolean> resolveProperty(String query, Boolean defaultValue) {
        String value = getEnv(toEnvironmentVariableName(query), runtimeProperties.getString(query));
        if (StringUtils.isBlank(value)) {
            return config.map(config -> {
                if (!isLeaf(query)) {
                    JsonObject leaf = getLeaf(query, config);
                    return leaf.getBoolean(getLeafPropertyName(query), defaultValue);
                }
                return config.getBoolean(query, defaultValue);
            });
        }

        return Single.just(Boolean.parseBoolean(value));
    }

    public Single<String> resolveProperty(String query, String defaultValue) {
        String value = getEnv(toEnvironmentVariableName(query), runtimeProperties.getString(query));
        if (StringUtils.isBlank(value)) {
            return config.map(config -> {
                if (!isLeaf(query)) {
                    JsonObject leaf = getLeaf(query, config);
                    return leaf.getString(getLeafPropertyName(query), defaultValue);
                }
                return config.getString(query, defaultValue);
            });
        }

        return Single.just(value);
    }

    public Single<Integer> resolveProperty(String query, int defaultValue) {
        String value = getEnv(toEnvironmentVariableName(query), runtimeProperties.getString(query));
        if (StringUtils.isBlank(value)) {
            return config.map(config -> {
                if (!isLeaf(query)) {
                    JsonObject leaf = getLeaf(query, config);
                    return leaf.getInteger(getLeafPropertyName(query), defaultValue);
                }
                return config.getInteger(query, defaultValue);
            });
        }

        return Single.just(Integer.parseInt(value));
    }

    public Single<JsonObject> getConfig() {
        return config;
    }

    private String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }

        return value;
    }

    private String toEnvironmentVariableName(String name) {
        return ENV_PREFIX + ENV_SEPARATOR + name.replace(".", ENV_SEPARATOR).toUpperCase();
    }

    private ConfigStoreOptions getConfigStoreOpt(FileType type) {
        return new ConfigStoreOptions()
                .setType("file")
                .setFormat(type.name().toLowerCase())
                .setConfig(new JsonObject().put("path", getConfigPath()));
    }

    private String getConfigPath() {
        String configPath = getEnv("VERTX_CONFIG_PATH", runtimeProperties.getString("path"));
        if (StringUtils.isBlank(configPath)) {
            LOG.warn("Missing VERTX_CONFIG_PATH environment variable. Set config default path to simpleTest-config.yaml");
            configPath = DEFAULT_CONFIG_PATH;
        }
        return configPath;
    }

    private boolean isLeaf(String query) {
        return !query.contains(".");
    }

    private String getLeafPropertyName(String query) {
        String[] queryNames = query.split("\\.");
        return queryNames[queryNames.length - 1];
    }

    private JsonObject getLeaf(String query, JsonObject config) {
        String[] queryNames = query.split("\\.");
        JsonObject leaf = config.getJsonObject(queryNames[0]);

        for (int i = 1; i < queryNames.length - 1; i++) {
            leaf = config.getJsonObject(queryNames[i]);
        }

        return leaf;
    }
}
