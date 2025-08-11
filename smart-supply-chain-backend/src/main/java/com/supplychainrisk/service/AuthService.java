package com.supplychainrisk.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public User getOrCreateUser(FirebaseToken decodedToken) {
        String firebaseUid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        String name = decodedToken.getName();

        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update email and name if they have changed
            boolean updated = false;
            if (!email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }
            if (!name.equals(user.getName())) {
                user.setName(name);
                updated = true;
            }
            if (updated) {
                return userRepository.save(user);
            }
            return user;
        } else {
            // Create new user with default VIEWER role
            User newUser = new User(firebaseUid, email, name, User.Role.VIEWER);
            return userRepository.save(newUser);
        }
    }

    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }
}