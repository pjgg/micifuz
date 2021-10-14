package com.micifuz.authn.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.micifuz.authn.model.Users;

import io.vertx.ext.web.handler.HttpException;

@ApplicationScoped
public class KeycloakService {

    private static final int RANDOM_PASSWORD_LENGTH = 7;
    private static final List<String> PETSHOP_DEFAULT_ROLES = List.of("user-role", "user-petshop");
    private static final List<String> VETS_DEFAULT_ROLES = List.of("user-role", "user-vets");
    private static final List<String> SHELTERS_DEFAULT_ROLES = List.of("user-role", "user-shelters");
    private static final Map<String, List<String>> DEFAULT_ROLES = Map.of("petshop-client-id", PETSHOP_DEFAULT_ROLES,
            "vets-client-id", VETS_DEFAULT_ROLES,
            "shelters-client-id", SHELTERS_DEFAULT_ROLES);

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    private String oauthServerUrl;

    @Inject
    private Keycloak keycloakCli;

    private String realm;

    @PostConstruct
    void initialize() {
        this.realm = oauthServerUrl.substring(oauthServerUrl.lastIndexOf('/') + 1);
    }

    public String createUser(Users u, String clientId) {
        var user = toKeycloakUser(u, Arrays.asList(defaultCredentials()), false, true);
        var response = keycloakCli.realm(realm).users().create(user);
        if (response.getStatus() != HttpStatus.SC_CREATED) {
            throw new HttpException(response.getStatus(), "Auth user not saved");
        }

        var userId = getUserId(keycloakCli, realm, u.userName());
        var existingRoles = getExistingRoles(keycloakCli, realm);
        removeRoles(keycloakCli, realm, userId, existingRoles);
        assignRoles(keycloakCli, realm, userId, DEFAULT_ROLES.get(clientId));

        return userId;
    }

    public Users findUserByUserName(final String clientId, final String userName) {
        var roles = DEFAULT_ROLES.get(clientId);
        return keycloakCli.realm(realm).users().search(userName.toLowerCase(), true)
                .stream()
                .findFirst()
                .filter(u -> findRolesNames(keycloakCli, realm, u.getId()).containsAll(roles))
                .map(u -> new Users(u.isEnabled(), u.getUsername(), u.getFirstName(), u.getLastName(), u.getEmail()))
                .orElseThrow(() -> new HttpException(HttpStatus.SC_NOT_FOUND, String.format("User %s not found", userName)));
    }

    private List<String> findRolesNames(final Keycloak keycloakCli, final String realm, String userId) {
        return keycloakCli.realm(realm)
                .users()
                .get(userId)
                .roles().realmLevel().listEffective()
                .stream().map(RoleRepresentation::getName).collect(Collectors.toList());
    }

    private void assignRoles(final Keycloak keycloakCli, final String realm, String userId, List<String> roles) {
        List<RoleRepresentation> roleList = rolesToRealmRoleRepresentation(keycloakCli, realm, roles);
        keycloakCli.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(roleList);

    }

    private String getUserId(final Keycloak keycloakCli, final String realm, final String userName) {
        return keycloakCli.realm(realm).users().search(userName.toLowerCase(), true)
                .stream()
                .map(UserRepresentation::getId)
                .findFirst()
                .orElseThrow(() -> new HttpException(HttpStatus.SC_NOT_FOUND, String.format("User %s not found", userName)));
    }

    private UserRepresentation toKeycloakUser(Users user, List<CredentialRepresentation> credentials, boolean emailVerified,
            boolean enabled) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.userName());
        userRepresentation.setFirstName(user.firstName());
        userRepresentation.setLastName(user.lastName());
        userRepresentation.setEmail(user.email());
        userRepresentation.setEmailVerified(emailVerified);
        userRepresentation.setCredentials(credentials);
        userRepresentation.setEnabled(enabled);

        return userRepresentation;
    }

    private List<RoleRepresentation> rolesToRealmRoleRepresentation(final Keycloak keycloakCli, final String realm,
            List<String> roles) {
        List<RoleRepresentation> resultRoles = new ArrayList<>();
        var existingRoles = getExistingRoles(keycloakCli, realm);
        var serverRoles = existingRoles.stream().map(RoleRepresentation::getName).collect(Collectors.toList());

        for (var role : roles) {
            int index = serverRoles.indexOf(role);
            if (index != -1) {
                resultRoles.add(existingRoles.get(index));
            }
        }

        return resultRoles;
    }

    private void removeRoles(final Keycloak keycloakCli, final String realm, String userId, List<RoleRepresentation> roleList) {
        keycloakCli.realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .remove(roleList);
    }

    private List<RoleRepresentation> getExistingRoles(final Keycloak keycloakCli, final String realm) {
        return keycloakCli.realm(realm).roles().list();
    }

    private CredentialRepresentation defaultCredentials() {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(true);
        credential.setValue(RandomStringUtils.random(RANDOM_PASSWORD_LENGTH, true, true));
        return credential;
    }
}
