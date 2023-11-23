package com.gritlab.service;

import com.gritlab.exception.InvalidParamException;
import com.gritlab.model.*;
import com.gritlab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Validated
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User convertFromDto(UserDTO userDTO) {
        User user = new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail(),
                userDTO.getPassword(), null);
        if (userDTO.getRole() != null) {
            user.setRole(Role.valueOf(userDTO.getRole()));
        }
        return user;
    }

    public UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(),
                user.getPassword(), null);
        if (user.getRole() != null) {
            userDTO.setRole(user.getRole().toString());
        }
        return userDTO;
    }

    public List<UserDTO> convertToDtos(List<User> users) {
        return users.stream()
                .map(this::convertToDto)
                .toList();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createAccount(UserRequest userRequest) {
        UserDTO userDTO = new UserDTO(null, userRequest.getName().replaceAll("\\s+", " ").trim(),
                userRequest.getEmail().trim().toLowerCase(), userRequest.getPassword(),
                userRequest.getRole().trim().toUpperCase());
        String userId;
        do {
            userId = UUID.randomUUID().toString().split("-")[0];
        } while (userRepository.existsById(userId));
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword() + userId);
        userDTO = UserDTO.builder()
                .id(userId)
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(hashedPassword)
                .role(userDTO.getRole())
                .build();

        if (emailExists(userDTO.getEmail())) {
            throw new InvalidParamException("Email already exists");
        }

        return userRepository.save(convertFromDto(userDTO));
    }

    public boolean findUserById(String userId) {
        return userRepository.existsById(userId);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User updateUser(String userId, UserRequest userRequest) {
        UserDTO userDTO = new UserDTO(null, userRequest.getName().replaceAll("\\s+", " ").trim(),
                userRequest.getEmail().trim().toLowerCase(), userRequest.getPassword(),
                userRequest.getRole().trim().toUpperCase());
        Optional<User> user = userRepository.findById(userId);
        String hashedPassword = null;
        if (userDTO.getPassword() != null) {
            hashedPassword = passwordEncoder.encode(userDTO.getPassword() + userId);
        } else if (user.isPresent()) {
            hashedPassword = user.get().getPassword();
        }
        userDTO = UserDTO.builder()
                .id(userId)
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(hashedPassword)
                .role(userDTO.getRole())
                .build();
        return userRepository.save(convertFromDto(userDTO));
    }

    public void deleteUser(String userId) {
        // todo: delete user's products
        userRepository.deleteById(userId);
    }

    public User authorizeUser(Authentication authentication, String userId) {
        if (userId != null && !findUserById(userId)) {
            throw new NoSuchElementException("User not found");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> user = getUserByEmail(userDetails.getUsername());
        if (user.isEmpty() || (userId != null && !user.get().getId().equals(userId))) {
            throw new BadCredentialsException("Operation is not allowed");
        }
        return user.get();
    }
}
