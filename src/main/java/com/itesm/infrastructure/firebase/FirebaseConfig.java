package com.itesm.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Startup
@ApplicationScoped
@UnlessBuildProfile("test")
public class FirebaseConfig {
    private static final Logger LOG = Logger.getLogger(FirebaseConfig.class);

    @ConfigProperty(name="firebase.service-account-location")
    Optional<String> path;

    @ConfigProperty(name="firebase.service-account-json")
    Optional<String> serviceAccountJson;

    @ConfigProperty(name="firebase.service-account-base64")
    Optional<String> serviceAccountBase64;

    @PostConstruct
    void init(){
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(resolveCredentials())
                        .build();

                FirebaseApp.initializeApp(options);
                LOG.info("Firebase inicializado");
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo inicializar Firebase Admin SDK", e);
        }
    }

    private GoogleCredentials resolveCredentials() throws IOException {
        Optional<String> json = serviceAccountJson.filter(value -> !value.isBlank());
        if (json.isPresent()) {
            return GoogleCredentials.fromStream(
                    new ByteArrayInputStream(json.get().getBytes(StandardCharsets.UTF_8))
            );
        }

        Optional<String> base64 = serviceAccountBase64.filter(value -> !value.isBlank());
        if (base64.isPresent()) {
            byte[] decoded = Base64.getDecoder().decode(base64.get());
            return GoogleCredentials.fromStream(new ByteArrayInputStream(decoded));
        }

        Optional<String> location = path.filter(value -> !value.isBlank());
        if (location.isPresent()) {
            try (InputStream serviceAccount = openServiceAccount(location.get())) {
                return GoogleCredentials.fromStream(serviceAccount);
            }
        }

        return GoogleCredentials.getApplicationDefault();
    }

    private InputStream openServiceAccount(String location) throws IOException {
        String resourceName = location.startsWith("classpath:")
                ? location.substring("classpath:".length())
                : location;

        InputStream resource = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourceName);

        if (resource != null) {
            return resource;
        }

        return new FileInputStream(location);
    }
}
