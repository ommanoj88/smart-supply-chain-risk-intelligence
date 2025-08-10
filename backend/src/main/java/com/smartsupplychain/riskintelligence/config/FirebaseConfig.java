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
            FirebaseOptions options;
            
            if (privateKey.isEmpty() || clientEmail.isEmpty()) {
                // Use default credentials for development
                options = FirebaseOptions.builder()
                        .setProjectId(projectId)
                        .build();
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
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}