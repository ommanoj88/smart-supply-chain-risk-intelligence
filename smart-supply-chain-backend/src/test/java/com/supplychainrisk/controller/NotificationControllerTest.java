package com.supplychainrisk.controller;

import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.AdvancedNotificationService;
import com.supplychainrisk.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private AdvancedNotificationService advancedNotificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testSendAdvancedNotification() throws Exception {
        // Arrange
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Test Notification");
        requestBody.put("content", "Test content");
        requestBody.put("channels", new String[]{"email", "slack"});

        // Act & Assert
        mockMvc.perform(post("/api/notifications/advanced/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Advanced notification sent successfully"));
    }

    @Test
    void testGetDeliveryStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notifications/delivery-status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notificationId").value(1))
                .andExpect(jsonPath("$.status").value("delivered"));
    }

    @Test
    void testUpdateNotificationPreferences() throws Exception {
        // Arrange
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("emailEnabled", true);
        preferences.put("slackEnabled", false);

        // Act & Assert
        mockMvc.perform(post("/api/notifications/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Notification preferences updated"));
    }

    @Test
    void testSendAdvancedNotificationUnauthorized() throws Exception {
        // Arrange - clear security context
        SecurityContextHolder.clearContext();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Test Notification");

        // Act & Assert
        mockMvc.perform(post("/api/notifications/advanced/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }
}