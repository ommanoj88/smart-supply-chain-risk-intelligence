package com.smartsupplychain.riskintelligence.service;

import com.smartsupplychain.riskintelligence.dto.UserDto;
import com.smartsupplychain.riskintelligence.enums.UserRole;
import com.smartsupplychain.riskintelligence.exception.ResourceNotFoundException;
import com.smartsupplychain.riskintelligence.model.User;
import com.smartsupplychain.riskintelligence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getActiveUsers() {
        return userRepository.findByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return convertToDto(user);
    }

    public UserDto getUserByFirebaseUid(String firebaseUid) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "firebaseUid", firebaseUid));
        return convertToDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return convertToDto(user);
    }

    public UserDto createUser(String firebaseUid, String email, String name, UserRole role) {
        if (userRepository.existsByFirebaseUid(firebaseUid)) {
            throw new IllegalArgumentException("User with Firebase UID already exists: " + firebaseUid);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email already exists: " + email);
        }

        User user = new User(firebaseUid, email, name, role != null ? role : UserRole.VIEWER);
        user = userRepository.save(user);
        return convertToDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
        if (userDto.getIsActive() != null) {
            user.setIsActive(userDto.getIsActive());
        }

        user = userRepository.save(user);
        return convertToDto(user);
    }

    public UserDto createOrUpdateUserFromFirebase(String firebaseUid, String email, String name) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .map(existingUser -> {
                    // Update existing user if needed
                    if (name != null && !name.equals(existingUser.getName())) {
                        existingUser.setName(name);
                        userRepository.save(existingUser);
                    }
                    return convertToDto(existingUser);
                })
                .orElseGet(() -> createUser(firebaseUid, email, name, UserRole.VIEWER));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    public List<UserDto> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}