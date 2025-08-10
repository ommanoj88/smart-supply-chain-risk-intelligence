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
     * 1. Service account key file (if available and valid)
     * 2. Default credentials (for production environments)
     * 3. Skips initialization for development with placeholder credentials
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
                        // Check if this is a placeholder/development key
                        String content = new String(serviceAccount.readAllBytes());
                        if (content.contains("placeholder") || content.contains("abc123")) {
                            logger.warn("Placeholder Firebase service account detected. Firebase initialization skipped for development.");
                            logger.warn("To enable Firebase, provide a valid service-account-key.json file.");
                            return;
                        }
                        
                        // Reset stream for actual credential loading
                        try (InputStream resetStream = serviceAccountKeyPath.getInputStream()) {
                            GoogleCredentials credentials = GoogleCredentials.fromStream(resetStream);
                            optionsBuilder.setCredentials(credentials);
                            logger.info("Firebase initialized with service account key file");
                        }
                    }
                } else {
                    // Try to use default credentials (for production environments)
                    try {
                        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
                        optionsBuilder.setCredentials(credentials);
                        logger.info("Firebase initialized with application default credentials");
                    } catch (IOException e) {
                        logger.warn("No Firebase credentials found. Firebase initialization skipped.");
                        logger.warn("For production use, set up proper Firebase credentials.");
                        return;
                    }
                }
                
                FirebaseOptions options = optionsBuilder.build();
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK initialized successfully for project: {}", projectId);
                
            } else {
                logger.info("Firebase Admin SDK is already initialized");
            }
            
        } catch (IOException e) {
            logger.warn("Failed to initialize Firebase Admin SDK: {}. Application will continue without Firebase.", e.getMessage());
            // Don't throw exception - allow application to start without Firebase for development
        } catch (Exception e) {
            logger.error("Unexpected error during Firebase initialization", e);
            throw new IllegalStateException("Firebase initialization failed", e);
        }
    }
    
    /**
     * Creates a FirebaseAuth bean for dependency injection.
     * 
     * This bean provides access to Firebase Authentication services
     * for token verification and user management. Returns null if
     * Firebase is not properly initialized (development mode).
     * 
     * @return FirebaseAuth instance or null if Firebase not initialized
     */
    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            if (isFirebaseInitialized()) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                logger.debug("FirebaseAuth bean created successfully");
                return auth;
            } else {
                logger.warn("Firebase not initialized. FirebaseAuth bean will be null.");
                logger.warn("Authentication endpoints will not work without proper Firebase configuration.");
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to create FirebaseAuth bean: {}", e.getMessage());
            logger.warn("FirebaseAuth bean will be null. Authentication may not work properly.");
            return null;
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