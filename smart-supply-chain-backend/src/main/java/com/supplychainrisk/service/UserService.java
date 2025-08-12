package com.supplychainrisk.service;

import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userRepository.findActiveUserByUsernameOrEmail(identifier);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public User createUser(String username, String email, String password, String firstName, String lastName, User.Role role) {
        if (existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        if (existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, email, encodedPassword, firstName, lastName, role);
        user.setEmailVerified(false);
        user.setIsActive(true);
        
        return userRepository.save(user);
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public void updateLastLogin(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setLastLoginAt(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
    }
    
    public void incrementFailedLoginAttempts(String identifier) {
        Optional<User> userOptional = findByUsernameOrEmail(identifier);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            
            // Lock account after 5 failed attempts for 30 minutes
            if (attempts >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
            }
            
            userRepository.save(user);
        }
    }
    
    public boolean isAccountLocked(String identifier) {
        Optional<User> userOptional = findByUsernameOrEmail(identifier);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public User updateUserRole(String firebaseUid, User.Role role) {
        Optional<User> userOptional = userRepository.findByFirebaseUid(firebaseUid);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with Firebase UID: " + firebaseUid);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByFirebaseUid(String firebaseUid) {
        return userRepository.existsByFirebaseUid(firebaseUid);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public List<User> getUsersWithRole(User.Role role) {
        return userRepository.findByRole(role);
    }
}