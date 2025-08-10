package com.smartsupplychain.riskintelligence.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.private-key:}")
    private String privateKey;

    @Value("${firebase.client-email:}")
    private String clientEmail;

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options;
                
                if (privateKey.isEmpty() || clientEmail.isEmpty()) {
                    // For development, use application default credentials or create minimal config
                    try {
                        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
                        options = FirebaseOptions.builder()
                                .setCredentials(credentials)
                                .setProjectId(projectId)
                                .build();
                    } catch (Exception e) {
                        // If no default credentials available, create a mock configuration for development
                        System.out.println("Warning: Firebase not configured with valid credentials. Using mock configuration for development.");
                        // Don't initialize Firebase if credentials are not available
                        return;
                    }
                } else {
                    // Use service account credentials for production
                    String serviceAccount = String.format(
                        "{\n" +
                        "  \"type\": \"service_account\",\n" +
                        "  \"project_id\": \"%s\",\n" +
                        "  \"private_key\": \"%s\",\n" +
                        "  \"client_email\": \"%s\"\n" +
                        "}", projectId, privateKey.replace("\\n", "\n"), clientEmail);

                    GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(serviceAccount.getBytes(StandardCharsets.UTF_8))
                    );

                    options = FirebaseOptions.builder()
                            .setCredentials(credentials)
                            .setProjectId(projectId)
                            .build();
                }

                FirebaseApp.initializeApp(options);
            } catch (Exception e) {
                System.err.println("Failed to initialize Firebase: " + e.getMessage());
                // Don't fail the application startup, just log the error
            }
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            return FirebaseAuth.getInstance();
        } catch (Exception e) {
            System.err.println("Warning: Firebase Auth not available: " + e.getMessage());
            return null;
        }
    }
}