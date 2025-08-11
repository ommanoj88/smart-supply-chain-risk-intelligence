package com.supplychainrisk.service;

import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

    public User saveUser(User user) {
        return userRepository.save(user);
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
}