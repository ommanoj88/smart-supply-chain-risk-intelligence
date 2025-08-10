package com.supplychain.risk.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    @Value("${spring.firebase.project-id:rentvat}")
    private String projectId;
    
    @Bean
    @Profile("!dev")
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // Try to load service account key from classpath
                ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
                if (resource.exists()) {
                    InputStream serviceAccount = resource.getInputStream();
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setProjectId(projectId)
                            .build();
                    return FirebaseApp.initializeApp(options);
                } else {
                    // Use default credentials for production (Google Cloud environment)
                    logger.info("Firebase service account file not found, using default credentials");
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .setProjectId(projectId)
                            .build();
                    return FirebaseApp.initializeApp(options);
                }
            } catch (Exception e) {
                logger.error("Failed to initialize Firebase", e);
                throw new RuntimeException("Firebase initialization failed", e);
            }
        }
        return FirebaseApp.getInstance();
    }
    
    @Bean
    @Profile("!dev")
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
    
    @Bean
    @Profile("dev")
    public FirebaseApp mockFirebaseApp() {
        logger.warn("Using mock Firebase configuration for development");
        return null; // Return null to disable Firebase in development
    }
    
    @Bean
    @Profile("dev")
    public FirebaseAuth mockFirebaseAuth() {
        logger.warn("Using mock Firebase Auth for development");
        return null; // Return null to disable Firebase Auth in development
    }
}