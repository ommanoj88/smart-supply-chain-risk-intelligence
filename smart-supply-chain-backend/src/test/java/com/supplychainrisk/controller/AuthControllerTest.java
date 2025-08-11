package com.supplychainrisk.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testVerifyTokenEndpointRequiresToken() throws Exception {
        // Test that the verify endpoint returns 400 for missing token
        mockMvc.perform(post("/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ID token is required"));
    }

    @Test
    public void testVerifyTokenEndpointRejectsEmptyToken() throws Exception {
        // Test that the verify endpoint returns 400 for empty token
        mockMvc.perform(post("/auth/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ID token is required"));
    }

    @Test
    public void testGetCurrentUserRequiresAuthentication() throws Exception {
        // Test that the user endpoint requires authentication
        mockMvc.perform(get("/auth/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUserRoleRequiresAuthentication() throws Exception {
        // Test that the update role endpoint requires authentication
        mockMvc.perform(put("/auth/user/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firebaseUid\":\"test\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isUnauthorized());
    }
}