package com.itesm.infrastructure.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.itesm.application.port.identity.IdentityProviderGateway;
import com.itesm.application.port.identity.IdentityUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class FirebaseIdentityProviderGateway implements IdentityProviderGateway {

    @Override
    public IdentityUser createUser(
            String email,
            String password,
            String displayName
    ) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisplayName(displayName)
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            return new IdentityUser(
                    userRecord.getUid(),
                    userRecord.getEmail()
            );

        } catch (FirebaseAuthException e) {
            throw new BadRequestException("Error creating user in Firebase: " + e.getMessage());
        }
    }

    @Override
    public void updateUser(
            String firebaseUuid,
            String email,
            String displayName,
            Boolean disabled
    ) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(firebaseUuid);

            if (email != null && !email.isBlank()) {
                request.setEmail(email);
            }

            if (displayName != null && !displayName.isBlank()) {
                request.setDisplayName(displayName);
            }

            if (disabled != null) {
                request.setDisabled(disabled);
            }

            FirebaseAuth.getInstance().updateUser(request);

        } catch (FirebaseAuthException e) {
            throw new BadRequestException("Error updating user in Firebase: " + e.getMessage());
        }
    }

    @Override
    public void disableUser(String firebaseUuid) {
        updateUser(firebaseUuid, null, null, true);
    }

    @Override
    public void enableUser(String firebaseUuid) {
        updateUser(firebaseUuid, null, null, false);
    }

    @Override
    public void deleteUser(String firebaseUuid) {
        try {
            FirebaseAuth.getInstance().deleteUser(firebaseUuid);
        } catch (FirebaseAuthException e) {
            throw new BadRequestException("Error deleting user in Firebase: " + e.getMessage());
        }
    }

    @Override
    public void updatePassword(String firebaseUuid, String newPassword) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(firebaseUuid)
                    .setPassword(newPassword);

            FirebaseAuth.getInstance().updateUser(request);

        } catch (FirebaseAuthException e) {
            throw new BadRequestException("Error updating user password in Firebase: " + e.getMessage());
        }
    }

    @Override
    public void revokeRefreshTokens(String firebaseUuid) {
        try {
            FirebaseAuth.getInstance().revokeRefreshTokens(firebaseUuid);

        } catch (FirebaseAuthException e) {
            throw new BadRequestException("Error revoking user sessions in Firebase: " + e.getMessage());
        }
    }
}
