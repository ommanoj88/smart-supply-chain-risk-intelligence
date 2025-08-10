package com.supplychain.risk.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Firebase configuration class for initializing Firebase Admin SDK.
 * 
 * This configuration sets up Firebase authentication services for the
 * Smart Supply Chain Risk Intelligence platform. It initializes the
 * Firebase Admin SDK with service account credentials.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    /**
     * Path to the Firebase service account key file.
     */
    @Value("${firebase.service-account-key-path:classpath:service-account-key.json}")
    private Resource serviceAccountKeyPath;
    
    /**
     * Firebase project ID.
     */
    @Value("${firebase.project-id:smart-supply-chain-dev}")
    private String projectId;
    
    /**
     * Initializes Firebase Admin SDK with service account credentials.
     * 
     * This method is called after dependency injection is complete.
     * It attempts to initialize Firebase using either:
     * 1. Service account key file (if available)
     * 2. Default credentials (for production environments)
     * 
     * @throws IllegalStateException if Firebase initialization fails
     */
    @PostConstruct
    public void initializeFirebase() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                        .setProjectId(projectId);
                
                // Try to load service account key file
                if (serviceAccountKeyPath.exists()) {
                    try (InputStream serviceAccount = serviceAccountKeyPath.getInputStream()) {
                        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                        optionsBuilder.setCredentials(credentials);
                        logger.info("Firebase initialized with service account key file");
                    }
                } else {
                    // Use default credentials (for production environments)
                    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
                    optionsBuilder.setCredentials(credentials);
                    logger.info("Firebase initialized with application default credentials");
                }
                
                FirebaseOptions options = optionsBuilder.build();
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK initialized successfully for project: {}", projectId);
                
            } else {
                logger.info("Firebase Admin SDK is already initialized");
            }
            
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase Admin SDK", e);
            throw new IllegalStateException("Firebase initialization failed", e);
        }
    }
    
    /**
     * Creates a FirebaseAuth bean for dependency injection.
     * 
     * This bean provides access to Firebase Authentication services
     * for token verification and user management.
     * 
     * @return FirebaseAuth instance
     * @throws IllegalStateException if Firebase is not initialized
     */
    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            logger.debug("FirebaseAuth bean created successfully");
            return auth;
        } catch (Exception e) {
            logger.error("Failed to create FirebaseAuth bean", e);
            throw new IllegalStateException("FirebaseAuth initialization failed", e);
        }
    }
    
    /**
     * Gets the Firebase project ID.
     * 
     * @return the configured Firebase project ID
     */
    public String getProjectId() {
        return projectId;
    }
    
    /**
     * Checks if Firebase is properly initialized.
     * 
     * @return true if Firebase is initialized, false otherwise
     */
    public boolean isFirebaseInitialized() {
        return !FirebaseApp.getApps().isEmpty();
    }
}