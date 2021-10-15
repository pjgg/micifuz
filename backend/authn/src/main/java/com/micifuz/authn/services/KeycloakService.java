package com.micifuz.authn.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.micifuz.authn.model.Users;
import com.micifuz.authn.utils.RealmUtils;

import io.vertx.ext.web.handler.HttpException;

@ApplicationScoped
public class KeycloakService {

    private static final int RANDOM_PASSWORD_LENGTH = 7;

    @Inject
    @Named("admin")
    private Keycloak keycloakCli;

    @Inject
    private RealmUtils realmUtils;

    public String createUser(Users u, String clientId) {
        var user = toKeycloakUser(u, Arrays.asList(defaultCredentials()), false, true);
        var realm = realmUtils.getRealmByClientId(clientId);
        var response = keycloakCli.realm(realm).users().create(user);
        if (response.getStatus() != HttpStatus.SC_CREATED) {
            throw new HttpException(response.getStatus(), "Auth user not saved");
        }

        var userId = getUserId(keycloakCli, realm, u.userName());
        var existingRoles = getExistingRoles(keycloakCli, realm);
        removeRoles(keycloakCli, realm, userId, existingRoles);
        assignRoles(keycloakCli, realm, userId, realmUtils.getDefaultClientIdRoles().get(clientId));

        return userId;
    }

    public Users findUserByUserName(final String clientId, final String userName) {
        var roles = realmUtils.getDefaultClientIdRoles().get(clientId);
        var realm = realmUtils.getRealmByClientId(clientId);
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
