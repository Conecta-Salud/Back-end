package com.itesm.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Startup
@ApplicationScoped
@UnlessBuildProfile("test")
public class FirebaseConfig {

    private static final Logger LOG = Logger.getLogger(FirebaseConfig.class);

    @ConfigProperty(name = "firebase.service-account-location", defaultValue = "")
    String path;

    @PostConstruct
    void init() {
        String serviceAccountLocation = requireServiceAccountLocation(path);
        Path serviceAccountPath = requireExistingServiceAccountFile(serviceAccountLocation);

        LOG.infof("Firebase service account location configured: %s", serviceAccountPath);

        if (!FirebaseApp.getApps().isEmpty()) {
            LOG.info("Firebase already initialized.");
            return;
        }

        try (InputStream serviceAccount = Files.newInputStream(serviceAccountPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp app = FirebaseApp.initializeApp(options);

            logFirebaseInitialized(app, credentials);
        } catch (IOException | RuntimeException e) {
            LOG.error("Firebase initialization failed. Check the configured service account location and credentials.", e);
            throw new IllegalStateException(
                    "Firebase Admin SDK could not be initialized. Check FIREBASE_SERVICE_ACCOUNT and firebase.service-account-location.",
                    e
            );
        }
    }

    private String requireServiceAccountLocation(String configuredPath) {
        if (configuredPath == null || configuredPath.isBlank()) {
            throw new IllegalStateException(
                    "firebase.service-account-location is required. Configure it with FIREBASE_SERVICE_ACCOUNT."
            );
        }

        return configuredPath.trim();
    }

    private Path requireExistingServiceAccountFile(String configuredPath) {
        Path serviceAccountPath;

        try {
            serviceAccountPath = Paths.get(configuredPath).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new IllegalStateException("firebase.service-account-location is not a valid filesystem path.", e);
        }

        if (!Files.isRegularFile(serviceAccountPath)) {
            throw new IllegalStateException(
                    "Firebase service account file does not exist at configured path: " + serviceAccountPath
            );
        }

        return serviceAccountPath;
    }

    private void logFirebaseInitialized(FirebaseApp app, GoogleCredentials credentials) {
        String projectId = projectId(app, credentials);

        if (projectId == null || projectId.isBlank()) {
            LOG.info("Firebase initialized successfully.");
            return;
        }

        LOG.infof("Firebase initialized successfully. projectId=%s", projectId);
    }

    private String projectId(FirebaseApp app, GoogleCredentials credentials) {
        if (app.getOptions() != null && app.getOptions().getProjectId() != null) {
            return app.getOptions().getProjectId();
        }

        if (credentials instanceof ServiceAccountCredentials serviceAccountCredentials) {
            return serviceAccountCredentials.getProjectId();
        }

        return null;
    }
}
