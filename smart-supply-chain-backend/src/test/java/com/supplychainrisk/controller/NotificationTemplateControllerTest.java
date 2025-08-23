package com.supplychainrisk.controller;

import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.NotificationTemplateService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationTemplateController.class)
public class NotificationTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationTemplateService templateService;

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
    void testGetTemplates() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notification-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Risk Alert Email Template"));
    }

    @Test
    void testGetTemplatesWithFilters() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notification-templates")
                .param("category", "RISK_ALERT")
                .param("channel", "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testGetTemplate() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notification-templates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Risk Alert Email Template"))
                .andExpect(jsonPath("$.category").value("RISK_ALERT"))
                .andExpect(jsonPath("$.channel").value("EMAIL"));
    }

    @Test
    void testCreateTemplate() throws Exception {
        // Arrange
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", "New Template");
        templateData.put("category", "SUPPLIER_UPDATE");
        templateData.put("channel", "EMAIL");
        templateData.put("content", "Template content");

        // Act & Assert
        mockMvc.perform(post("/api/notification-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(templateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Template"))
                .andExpect(jsonPath("$.category").value("SUPPLIER_UPDATE"))
                .andExpect(jsonPath("$.channel").value("EMAIL"));
    }

    @Test
    void testCreateTemplateValidationError() throws Exception {
        // Arrange - missing required fields
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", "New Template");
        // Missing category, channel, content

        // Act & Assert
        mockMvc.perform(post("/api/notification-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(templateData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Missing required fields: name, category, channel, content"));
    }

    @Test
    void testUpdateTemplate() throws Exception {
        // Arrange
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", "Updated Template");
        templateData.put("category", "GENERAL");
        templateData.put("channel", "SLACK");
        templateData.put("content", "Updated content");

        // Act & Assert
        mockMvc.perform(put("/api/notification-templates/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(templateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Template"));
    }

    @Test
    void testDeleteTemplate() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/notification-templates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Template deleted successfully"));
    }

    @Test
    void testPreviewTemplate() throws Exception {
        // Arrange
        Map<String, Object> variables = new HashMap<>();
        variables.put("alertTitle", "Test Alert");
        variables.put("recipientName", "John Doe");
        variables.put("alertDescription", "Test alert description");
        variables.put("severity", "HIGH");

        // Act & Assert
        mockMvc.perform(post("/api/notification-templates/1/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(variables)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templateId").value(1))
                .andExpect(jsonPath("$.renderedSubject").exists())
                .andExpect(jsonPath("$.renderedContent").exists())
                .andExpect(jsonPath("$.variables").exists());
    }

    @Test
    void testGetTemplateCategories() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notification-templates/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("RISK_ALERT"))
                .andExpect(jsonPath("$[1]").value("CRITICAL_ALERT"));
    }

    @Test
    void testGetNotificationChannels() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notification-templates/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("EMAIL"))
                .andExpect(jsonPath("$[1]").value("SLACK"));
    }

    @Test
    void testCreateTemplateUnauthorized() throws Exception {
        // Arrange - clear security context
        SecurityContextHolder.clearContext();

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", "New Template");

        // Act & Assert
        mockMvc.perform(post("/api/notification-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(templateData)))
                .andExpect(status().isUnauthorized());
    }
}