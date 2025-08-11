package com.supplychainrisk.repository;

import com.supplychainrisk.entity.UserSession;
import com.supplychainrisk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    Optional<UserSession> findByTokenHash(String tokenHash);
    
    List<UserSession> findByUserAndIsRevokedFalse(User user);
    
    @Query("SELECT s FROM UserSession s WHERE s.tokenHash = :tokenHash AND s.isRevoked = false AND s.expiresAt > :now")
    Optional<UserSession> findValidSession(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isRevoked = true WHERE s.user = :user")
    void revokeAllUserSessions(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isRevoked = true WHERE s.expiresAt <= :now AND s.isRevoked = false")
    void revokeExpiredSessions(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt <= :cutoff")
    void deleteExpiredSessions(@Param("cutoff") LocalDateTime cutoff);
}