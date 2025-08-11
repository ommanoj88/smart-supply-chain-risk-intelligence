package com.supplychainrisk.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import com.supplychainrisk.service.AuthService;
import com.supplychainrisk.service.UserService;
import com.supplychainrisk.security.FirebaseAuthenticationFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthService authService;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @Test
    @WithMockUser
    public void testVerifyTokenEndpointIsAccessible() throws Exception {
        // Test that the verify endpoint is accessible (regardless of token validity since we're using mocks)
        mockMvc.perform(post("/auth/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk()); // With mocked services, this will return 200
    }

    @Test
    @WithMockUser
    public void testVerifyTokenEndpointAcceptsJson() throws Exception {
        // Test that the verify endpoint accepts JSON content
        mockMvc.perform(post("/auth/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\"test\"}"))
                .andExpect(status().isOk()); // With mocked services, this will return 200
    }
}