package com.supplychainrisk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Enhanced Security Configuration with OAuth 2.0 support
 * Supports both JWT and OAuth 2.0 resource server functionality
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class EnhancedSecurityConfig {

    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    @Value("${security.oauth2.enabled:false}")
    private boolean oauth2Enabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/health/**", "/actuator/**").permitAll()
                .requestMatchers("/auth/**", "/api/auth/**").permitAll()
                .requestMatchers("/mock-apis/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/ml/train/**").hasRole("ADMIN")
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                
                // Manager and above endpoints
                .requestMatchers("/api/suppliers/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/shipments/create", "/api/shipments/update/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .requestMatchers("/api/ml/analyze/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                
                // All authenticated users
                .requestMatchers("/api/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER", "VIEWER", "AUDITOR")
                .anyRequest().authenticated()
            );

        // Configure OAuth 2.0 if enabled
        if (oauth2Enabled) {
            http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse allowed origins
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOriginPatterns(origins);
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        // Expose headers for rate limiting
        configuration.setExposedHeaders(Arrays.asList(
            "X-Rate-Limit-Remaining",
            "X-Rate-Limit-Total", 
            "X-Rate-Limit-Reset",
            "Authorization"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        authenticationConverter.setPrincipalClaimName("sub");

        return authenticationConverter;
    }
}