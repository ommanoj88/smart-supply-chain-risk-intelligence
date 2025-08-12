package com.supplychainrisk.config;

import com.supplychainrisk.filter.SecurityHeadersFilter;
import com.supplychainrisk.security.FirebaseAuthenticationFilter;
import com.supplychainrisk.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                         JwtAuthenticationFilter jwtAuthenticationFilter,
                                         FirebaseAuthenticationFilter firebaseAuthenticationFilter,
                                         CsrfTokenRepository csrfTokenRepository,
                                         CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository)
                .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(firebaseAuthenticationFilter, JwtAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/health", "/public/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers("/auth/login", "/auth/register", "/auth/forgot-password", "/auth/reset-password").permitAll()
                .requestMatchers("/mock-apis/**").permitAll()
                .requestMatchers("/auth/verify", "/auth/user", "/auth/logout").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/supply-manager/**").hasAnyRole("ADMIN", "SUPPLY_MANAGER")
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // More restrictive CORS configuration
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "X-XSRF-TOKEN"));
        configuration.setExposedHeaders(Arrays.asList("X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}