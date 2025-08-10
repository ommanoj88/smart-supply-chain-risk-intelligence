package com.supply.chain.service;

import com.supply.chain.model.User;
import com.supply.chain.model.UserRole;
import com.supply.chain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    public User createOrUpdateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getUid());
        
        if (existingUser.isPresent()) {
            User existing = existingUser.get();
            existing.setEmail(user.getEmail());
            existing.setDisplayName(user.getDisplayName());
            existing.setPhotoURL(user.getPhotoURL());
            existing.setUpdatedAt(LocalDateTime.now());
            
            logger.info("Updated existing user: {}", existing.getEmail());
            return userRepository.save(existing);
        } else {
            logger.info("Creating new user: {}", user.getEmail());
            return userRepository.save(user);
        }
    }
    
    public Optional<User> getUserById(String uid) {
        return userRepository.findById(uid);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User updateUserRole(String uid, UserRole role) {
        Optional<User> userOpt = userRepository.findById(uid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(role);
            user.setUpdatedAt(LocalDateTime.now());
            
            logger.info("Updated role for user {} to {}", user.getEmail(), role);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with ID: " + uid);
    }
    
    public void deleteUser(String uid) {
        userRepository.deleteById(uid);
        logger.info("Deleted user with ID: {}", uid);
    }
}