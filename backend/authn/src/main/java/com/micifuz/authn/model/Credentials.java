package com.micifuz.authn.model;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.http.HttpServerRequest;

@RegisterForReflection
public record Credentials(
        String grantType,
        String clientId,
        String clientSecret,
        String audience,
        String userName,
        String userPwd,
        String scope,
        String realm,
        String authServerUrl) {

    private static final String PWD = "password";
    private static final String GRANT_TYPE = "grant_type";
    private static final String SCOPE = "scope";
    private static final String AUDIENCE = "audience";
    private static final String USER_NAME = "username";
    private static final String USER_PWD = "password";

    public Credentials(String grantType,
            String clientId,
            String clientSecret,
            String audience,
            String userName,
            String userPwd,
            String scope,
            String realm,
            String authServerUrl) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.scope = scope;
        this.realm = realm;
        this.authServerUrl = StringUtils.substringBefore(authServerUrl, "/realms");
        this.audience = audience;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public Credentials(String serverUrl, String clientId, String secret, HttpServerRequest request) {
        this(request.getFormAttribute(GRANT_TYPE),
                clientId,
                secret,
                request.getFormAttribute(AUDIENCE),
                request.getFormAttribute(USER_NAME),
                request.getFormAttribute(USER_PWD),
                request.getFormAttribute(SCOPE),
                serverUrl.substring(serverUrl.lastIndexOf('/') + 1),
                StringUtils.substringBefore(serverUrl, "/realms"));
    }

    public AccessTokenResponse createAccessToken() {
        KeycloakBuilder keycloakBuilder = newKeycloakBuilderWithClientCredentials();

        if (isPasswordFlow()) {
            keycloakBuilder = newKeycloakBuilderWithPasswordCredentials(keycloakBuilder, userName, userPwd);
        }

        return keycloakBuilder.build().tokenManager().getAccessToken();
    }

    private KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(KeycloakBuilder builder, String username,
            String password) {
        return builder
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD);
    }

    private KeycloakBuilder newKeycloakBuilderWithClientCredentials() {
        return KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(authServerUrl)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS);
    }

    private boolean isPasswordFlow() {
        return grantType.equals(PWD);
    }
}
