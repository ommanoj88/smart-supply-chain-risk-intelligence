package com.supplychainrisk.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationFilter.class);

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7);
            
            try {
                FirebaseToken decodedToken = authService.verifyToken(idToken);
                User user = authService.getOrCreateUser(decodedToken);
                
                // Create authentication token with user role
                String authority = "ROLE_" + user.getRole().name();
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        user, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority(authority))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Add user to request attributes for easy access
                request.setAttribute("currentUser", user);
                
            } catch (FirebaseAuthException e) {
                logger.error("Firebase token verification failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid authentication token\"}");
                response.setContentType("application/json");
                return;
            } catch (Exception e) {
                logger.error("Authentication error: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Authentication error\"}");
                response.setContentType("application/json");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Skip filter for public endpoints
        return path.equals("/") || 
               path.startsWith("/public/") || 
               path.equals("/health") ||
               (path.equals("/auth/verify") && "OPTIONS".equals(request.getMethod()));
    }
}