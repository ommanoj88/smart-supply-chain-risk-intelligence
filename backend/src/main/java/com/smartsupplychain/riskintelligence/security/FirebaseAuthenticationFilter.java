package com.smartsupplychain.riskintelligence.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationFilter(@Autowired(required = false) FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");
        
        if (firebaseAuth != null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            try {
                FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);
                String uid = firebaseToken.getUid();
                String email = firebaseToken.getEmail();
                
                // Create authentication object
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(uid, null, authorities);
                
                // Set additional details
                FirebaseUserPrincipal principal = new FirebaseUserPrincipal(uid, email, firebaseToken.getClaims());
                authentication.setDetails(principal);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (FirebaseAuthException e) {
                logger.error("Firebase token verification failed", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid token\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/h2-console") || 
               path.equals("/api/auth/health") ||
               path.equals("/api/health");
    }
}