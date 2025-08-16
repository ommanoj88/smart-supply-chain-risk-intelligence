package com.supplychainrisk.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path:firebase-service-account-test.json}")
    private String firebaseConfigPath;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            logger.info("Firebase is disabled. Skipping initialization.");
            return;
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Check if the file exists
                ClassPathResource resource = new ClassPathResource(firebaseConfigPath);
                if (!resource.exists()) {
                    logger.warn("Firebase configuration file '{}' not found. Firebase functionality will be disabled.", firebaseConfigPath);
                    return;
                }

                // Load the file from classpath
                InputStream serviceAccount = resource.getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully with config: {}", firebaseConfigPath);
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase with config '{}': {}", firebaseConfigPath, e.getMessage());
            logger.warn("Firebase functionality will be disabled. Application will continue without Firebase.");
        }
    }

    public boolean isFirebaseEnabled() {
        return firebaseEnabled && !FirebaseApp.getApps().isEmpty();
    }
}