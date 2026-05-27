package com.itesm.application.port.identity;

public interface IdentityProviderGateway {

    IdentityUser createUser(
            String email,
            String password,
            String displayName
    );

    void updateUser(
            String firebaseUuid,
            String email,
            String displayName,
            Boolean disabled
    );

    void disableUser(String firebaseUuid);

    void enableUser(String firebaseUuid);

    void deleteUser(String firebaseUuid);

    void updatePassword(String firebaseUuid, String newPassword);

    void revokeRefreshTokens(String firebaseUuid);
}
