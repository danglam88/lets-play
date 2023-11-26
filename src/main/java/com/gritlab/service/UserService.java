package com.gritlab.service;

import com.gritlab.exception.InvalidParamException;
import com.gritlab.model.*;
import com.gritlab.repository.UserRepository;
import com.gritlab.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User convertFromDto(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail(),
                userDTO.getPassword(), userDTO.getRole());
    }

    public UserDTO convertToDto(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(),
                user.getPassword(), user.getRole());
    }

    public List<UserDTO> convertToDtos(List<User> users) {
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDto)
                .toList();
        userDTOs.forEach(userDTO -> userDTO.setId(null));
        return userDTOs;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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

        String hashedPassword = passwordEncoder.encode(userDTO.getPassword() + userId);

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
        Optional<List<Product>> products = productRepository.findByUserId(userId);
        if (products.isPresent()) {
            for (Product product : products.get()) {
                productRepository.deleteById(product.getId());
            }
        }
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
