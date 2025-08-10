package com.supply.chain.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    @Value("${firebase.project-id}")
    private String projectId;
    
    @Value("${firebase.credentials-path}")
    private String credentialsPath;
    
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FileInputStream serviceAccount = new FileInputStream(credentialsPath);
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId(projectId)
                        .build();
                
                FirebaseApp app = FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully for project: {}", projectId);
                return app;
            } catch (Exception e) {
                logger.error("Failed to initialize Firebase. Make sure service-account-key.json exists at: {}", credentialsPath);
                logger.error("Error: {}", e.getMessage());
                logger.warn("Creating mock FirebaseApp for development - authentication will not work");
                
                // Create a mock FirebaseApp for development
                FirebaseOptions mockOptions = FirebaseOptions.builder()
                        .setProjectId(projectId)
                        .build();
                
                try {
                    return FirebaseApp.initializeApp(mockOptions, "mock-app");
                } catch (Exception mockError) {
                    throw new RuntimeException("Firebase initialization failed: " + e.getMessage());
                }
            }
        }
        return FirebaseApp.getInstance();
    }
}