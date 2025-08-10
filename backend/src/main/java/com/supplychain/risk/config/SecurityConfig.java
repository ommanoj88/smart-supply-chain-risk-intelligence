package com.supplychain.risk.config;

import com.supplychain.risk.security.FirebaseAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration for the Smart Supply Chain Risk Intelligence platform.
 * 
 * This configuration sets up:
 * - Firebase authentication integration
 * - Role-based access control (ADMIN, SUPPLY_MANAGER, VIEWER)
 * - CORS configuration for React frontend
 * - Stateless session management
 * - Custom authentication entry point
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    
    @Autowired
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;
    
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private List<String> allowedOrigins;
    
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private List<String> allowedMethods;
    
    @Value("${cors.allowed-headers:*}")
    private List<String> allowedHeaders;
    
    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;
    
    /**
     * Configures the main security filter chain.
     * 
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure session management (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/auth/verify").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                
                // Admin-only endpoints
                .requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasRole("ADMIN")
                
                // User management endpoints
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                
                // Supply chain management endpoints
                .requestMatchers(HttpMethod.GET, "/api/supply-chain/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER", "VIEWER")
                .requestMatchers(HttpMethod.POST, "/api/supply-chain/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/supply-chain/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/supply-chain/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                
                // Risk assessment endpoints
                .requestMatchers(HttpMethod.GET, "/api/risk/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER", "VIEWER")
                .requestMatchers(HttpMethod.POST, "/api/risk/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/risk/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/risk/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                
                // Analytics endpoints (read-only for viewers)
                .requestMatchers(HttpMethod.GET, "/api/analytics/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER", "VIEWER")
                .requestMatchers(HttpMethod.POST, "/api/analytics/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                
                // Authentication endpoints
                .requestMatchers("/api/auth/**").authenticated()
                
                // Profile endpoints (users can access their own profile)
                .requestMatchers(HttpMethod.GET, "/api/profile").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/profile").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure custom authentication entry point
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint())
            )
            
            // Add Firebase authentication filter
            .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Configure headers for H2 console (development only)
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );
        
        return http.build();
    }
    
    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * 
     * @return the CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            configuration.setAllowedOrigins(allowedOrigins);
        } else {
            // Default development origins
            configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://smart-supply-chain.netlify.app"
            ));
        }
        
        // Set allowed methods
        if (allowedMethods != null && !allowedMethods.isEmpty()) {
            configuration.setAllowedMethods(allowedMethods);
        } else {
            // Default methods
            configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
            ));
        }
        
        // Set allowed headers
        if (allowedHeaders != null && !allowedHeaders.isEmpty()) {
            configuration.setAllowedHeaders(allowedHeaders);
        } else {
            // Allow all headers
            configuration.addAllowedHeader("*");
        }
        
        // Set credentials support
        configuration.setAllowCredentials(allowCredentials);
        
        // Expose headers that frontend might need
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Set max age for preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Creates a custom authentication entry point for handling unauthorized access.
     * 
     * @return the AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
    
    /**
     * Custom authentication entry point that returns JSON error responses.
     */
    private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                           AuthenticationException authException) throws IOException {
            
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            
            String jsonResponse = """
                {
                    "status": 401,
                    "message": "Authentication required",
                    "error": {
                        "errorCode": "AUTHENTICATION_REQUIRED",
                        "path": "%s",
                        "method": "%s"
                    },
                    "timestamp": "%s"
                }
                """.formatted(
                    request.getRequestURI(),
                    request.getMethod(),
                    java.time.LocalDateTime.now().toString()
                );
            
            response.getWriter().write(jsonResponse);
        }
    }
}