package com.supplychain.risk.config;

import com.supplychain.risk.security.CustomAuthenticationEntryPoint;
import com.supplychain.risk.security.FirebaseAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;
    
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // Configure session management
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/").permitAll()
                
                // Development: Allow all dashboard endpoints for testing
                .requestMatchers("/api/dashboard/**").permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                
                // Manager and Admin endpoints
                .requestMatchers("/api/suppliers/create").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/suppliers/*/edit").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/suppliers/*/delete").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/shipments/create").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/shipments/*/edit").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/shipments/*/delete").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/risk/alerts/*/acknowledge").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/risk/assessments/create").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                
                // All authenticated users can read
                .requestMatchers("/api/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER", "VIEWER")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure authentication entry point
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(customAuthenticationEntryPoint))
            
            // Add Firebase authentication filter
            .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Allow H2 console frames (development only)
            .headers(headers -> headers.frameOptions().sameOrigin());
        
        return http.build();
    }
}