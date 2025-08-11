package com.supplychainrisk.service;

import com.supplychainrisk.entity.User;
import com.supplychainrisk.entity.UserSession;
import com.supplychainrisk.repository.UserSessionRepository;
import com.supplychainrisk.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserSessionService {
    
    @Autowired
    private UserSessionRepository userSessionRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public UserSession createSession(User user, String token, String ipAddress, String userAgent) {
        String tokenHash = jwtUtil.generateTokenHash(token);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1); // 24 hours
        
        UserSession session = new UserSession(user, tokenHash, expiresAt, ipAddress, userAgent);
        return userSessionRepository.save(session);
    }
    
    public Optional<UserSession> findValidSession(String token) {
        String tokenHash = jwtUtil.generateTokenHash(token);
        return userSessionRepository.findValidSession(tokenHash, LocalDateTime.now());
    }
    
    public boolean isValidSession(String token) {
        return findValidSession(token).isPresent();
    }
    
    public List<UserSession> getActiveUserSessions(User user) {
        return userSessionRepository.findByUserAndIsRevokedFalse(user);
    }
    
    @Transactional
    public void revokeSession(String token) {
        String tokenHash = jwtUtil.generateTokenHash(token);
        Optional<UserSession> sessionOptional = userSessionRepository.findByTokenHash(tokenHash);
        if (sessionOptional.isPresent()) {
            UserSession session = sessionOptional.get();
            session.setIsRevoked(true);
            userSessionRepository.save(session);
        }
    }
    
    @Transactional
    public void revokeAllUserSessions(User user) {
        userSessionRepository.revokeAllUserSessions(user);
    }
    
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        userSessionRepository.revokeExpiredSessions(now);
        
        // Delete sessions that expired more than 7 days ago
        LocalDateTime cutoff = now.minusDays(7);
        userSessionRepository.deleteExpiredSessions(cutoff);
    }
}