package com.supplychainrisk.dto;

import com.supplychainrisk.entity.User;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private UserDTO user;
    
    public AuthResponse() {}
    
    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public UserDTO getUser() {
        return user;
    }
    
    public void setUser(UserDTO user) {
        this.user = user;
    }
    
    // Nested UserDTO class for response
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;
        private String name;
        private String firstName;
        private String lastName;
        private User.Role role;
        private Boolean isActive;
        private Boolean emailVerified;
        
        public UserDTO() {}
        
        public UserDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.name = user.getName();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.role = user.getRole();
            this.isActive = user.getIsActive();
            this.emailVerified = user.getEmailVerified();
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public User.Role getRole() { return role; }
        public void setRole(User.Role role) { this.role = role; }
        
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        
        public Boolean getEmailVerified() { return emailVerified; }
        public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    }
}