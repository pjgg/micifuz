package com.micifuz.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class JwtAuthOptions {

    public static class Builder {
        private String jksPath;
        private JWTOptions jwtOpt;
        private String scopeDelimiter;
        private List<String> expectedScopes = new ArrayList<>();
        private WebClient webClient;

        public Builder(String jksPath, WebClient webClient) {
            this.jksPath = jksPath;
            this.webClient = webClient;
            this.scopeDelimiter = " ";
            this.jwtOpt = new JWTOptions().setAlgorithm("RS256");
        }

        public Builder withJWTOptions(JWTOptions jwtOpt) {
            this.jwtOpt = jwtOpt;
            return this;
        }

        public Builder withScopeDelimiter(String scopeDelimiter) {
            this.scopeDelimiter = scopeDelimiter;
            return this;
        }

        public Builder withExpectedScopes(List<String> expectedScopes) {
            this.expectedScopes = expectedScopes;
            return this;
        }

        public Builder withExpectedScope(String expectedScope) {
            this.expectedScopes.add(expectedScope);
            return this;
        }

        private List<Object> keysToList(JsonObject body) {
            return ((List<Object>) body.getJsonArray("keys").getList()).stream()
                    .map(o -> new JsonObject((Map<String, Object>) o))
                    .collect(Collectors.toList());
        }

        private Future<List<Object>> requestPublicKeys() {
            return webClient.getAbs(jksPath).as(BodyCodec.jsonObject())
                    .send().map(HttpResponse::body)
                    .map(this::keysToList);
        }

        public JwtAuthOptions build() {
            var jwtAuthOptions = new JwtAuthOptions();
            jwtAuthOptions.publicKeys = requestPublicKeys();
            jwtAuthOptions.jwtOpt = this.jwtOpt;
            jwtAuthOptions.expectedScopes = this.expectedScopes;
            jwtAuthOptions.scopeDelimiter = this.scopeDelimiter;
            return jwtAuthOptions;
        }
    }

    private JwtAuthOptions() {
    }

    private Future<List<Object>> publicKeys;
    private JWTOptions jwtOpt;
    private String scopeDelimiter;
    private List<String> expectedScopes;

    public Future<List<Object>> getPublicKeys() {
        return publicKeys;
    }

    public void setPublicKeys(Future<List<Object>> publicKeys) {
        this.publicKeys = publicKeys;
    }

    public JWTOptions getJwtOpt() {
        return jwtOpt;
    }

    public void setJwtOpt(JWTOptions jwtOpt) {
        this.jwtOpt = jwtOpt;
    }

    public String getScopeDelimiter() {
        return scopeDelimiter;
    }

    public void setScopeDelimiter(String scopeDelimiter) {
        this.scopeDelimiter = scopeDelimiter;
    }

    public List<String> getExpectedScopes() {
        return expectedScopes;
    }

    public void setExpectedScopes(List<String> expectedScopes) {
        this.expectedScopes = expectedScopes;
    }
}
