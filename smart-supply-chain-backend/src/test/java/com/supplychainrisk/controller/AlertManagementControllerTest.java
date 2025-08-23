package com.supplychainrisk.controller;

import com.supplychainrisk.entity.Alert;
import com.supplychainrisk.entity.AlertConfiguration;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.AlertRepository;
import com.supplychainrisk.repository.AlertConfigurationRepository;
import com.supplychainrisk.service.AdvancedNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertManagementController.class)
public class AlertManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvancedNotificationService advancedNotificationService;

    @MockBean
    private AlertRepository alertRepository;

    @MockBean
    private AlertConfigurationRepository alertConfigurationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private Alert mockAlert;
    private AlertConfiguration mockConfig;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        mockAlert = new Alert();
        mockAlert.setId(1L);
        mockAlert.setTitle("Test Alert");
        mockAlert.setStatus(Alert.AlertStatus.NEW);
        mockAlert.setCreatedAt(LocalDateTime.now());

        mockConfig = new AlertConfiguration();
        mockConfig.setId(1L);
        mockConfig.setName("Test Configuration");
        mockConfig.setAlertType(Alert.AlertType.RISK_ALERT);
        mockConfig.setEnabled(true);

        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAlerts() throws Exception {
        // Arrange
        List<Alert> alerts = Arrays.asList(mockAlert);
        Page<Alert> alertPage = new PageImpl<>(alerts);
        when(alertRepository.findAll(any(Pageable.class))).thenReturn(alertPage);

        // Act & Assert
        mockMvc.perform(get("/api/alert-management/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Alert"));
    }

    @Test
    void testGetAlert() throws Exception {
        // Arrange
        when(alertRepository.findById(1L)).thenReturn(Optional.of(mockAlert));

        // Act & Assert
        mockMvc.perform(get("/api/alert-management/alerts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Alert"));
    }

    @Test
    void testGetAlertNotFound() throws Exception {
        // Arrange
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/alert-management/alerts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAcknowledgeAlert() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("note", "Alert acknowledged by test user");
        
        doNothing().when(advancedNotificationService).acknowledgeAlert(eq(1L), eq("testuser"), any(String.class));

        // Act & Assert
        mockMvc.perform(post("/api/alert-management/alerts/1/acknowledge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Alert acknowledged successfully"));

        verify(advancedNotificationService).acknowledgeAlert(1L, "testuser", "Alert acknowledged by test user");
    }

    @Test
    void testResolveAlert() throws Exception {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("resolution", "Issue resolved");
        request.put("resolutionType", "FIXED");
        
        doNothing().when(advancedNotificationService).resolveAlert(eq(1L), eq("testuser"), any(String.class), any(Alert.ResolutionType.class));

        // Act & Assert
        mockMvc.perform(post("/api/alert-management/alerts/1/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Alert resolved successfully"));

        verify(advancedNotificationService).resolveAlert(1L, "testuser", "Issue resolved", Alert.ResolutionType.FIXED);
    }

    @Test
    void testGetAlertConfigurations() throws Exception {
        // Arrange
        List<AlertConfiguration> configs = Arrays.asList(mockConfig);
        when(alertConfigurationRepository.findAll()).thenReturn(configs);

        // Act & Assert
        mockMvc.perform(get("/api/alert-management/configurations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Configuration"));
    }

    @Test
    void testCreateAlertConfiguration() throws Exception {
        // Arrange
        when(alertConfigurationRepository.save(any(AlertConfiguration.class))).thenReturn(mockConfig);

        // Act & Assert
        mockMvc.perform(post("/api/alert-management/configurations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockConfig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Configuration"));

        verify(alertConfigurationRepository).save(any(AlertConfiguration.class));
    }

    @Test
    void testGetDashboardStats() throws Exception {
        // Arrange
        when(alertRepository.count()).thenReturn(100L);
        
        List<Object[]> statusCounts = Arrays.asList(
            new Object[]{Alert.AlertStatus.NEW, 20L},
            new Object[]{Alert.AlertStatus.ACKNOWLEDGED, 15L},
            new Object[]{Alert.AlertStatus.RESOLVED, 65L}
        );
        when(alertRepository.countAlertsByStatus()).thenReturn(statusCounts);

        // Act & Assert
        mockMvc.perform(get("/api/alert-management/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAlerts").value(100))
                .andExpect(jsonPath("$.newAlerts").value(20))
                .andExpect(jsonPath("$.acknowledgedAlerts").value(15))
                .andExpect(jsonPath("$.resolvedAlerts").value(65))
                .andExpect(jsonPath("$.activeAlerts").value(35));
    }

    @Test
    void testAcknowledgeAlertUnauthorized() throws Exception {
        // Arrange - clear security context
        SecurityContextHolder.clearContext();

        Map<String, String> request = new HashMap<>();
        request.put("note", "Test note");

        // Act & Assert
        mockMvc.perform(post("/api/alert-management/alerts/1/acknowledge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}