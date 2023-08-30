package com.LetsPlay.service;

import com.LetsPlay.model.Product;
import com.LetsPlay.model.UserDTO;
import com.LetsPlay.repository.ProductRepository;
import com.LetsPlay.repository.UserRepository;
import com.LetsPlay.model.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<UserDTO> convertToDtos(List<User> users) {
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public User createUser(User user) {
        if (user.getName() == null
                || user.getName().trim().isEmpty() || user.getName().trim().length() > 50
                || user.getEmail() == null
                || !user.hasValidEmail() || user.getEmail().trim().length() > 50
                || user.getPassword() == null
                || user.getPassword().length() < 6 || user.getPassword().length() > 50
                || user.getRole() == null
                || (!user.getRole().trim().equalsIgnoreCase("ROLE_ADMIN")
                && !user.getRole().trim().equalsIgnoreCase("ROLE_USER"))) {
            return null;
        }
        if (user.hasDuplicatedEmail(null)) {
            return new User();
        }
        String userId = "";
        do {
            userId = UUID.randomUUID().toString().split("-")[0];
        } while (userRepository.existsById(userId));
        user.setId(userId);
        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim());
        String salt = user.getId();
        String hashedPassword = passwordEncoder.encode(user.getPassword() + salt);
        user.setPassword(hashedPassword);
        user.setRole(user.getRole().trim().toUpperCase());
        return userRepository.save(user);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public boolean findUserById(String userId) {
        return userRepository.existsById(userId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public User updateUser(String userId, User user) {
        if (user.getName() == null
                || user.getName().trim().isEmpty() || user.getName().trim().length() > 50
                || user.getEmail() == null
                || !user.hasValidEmail() || user.getEmail().trim().length() > 50
                || user.getPassword() == null
                || user.getPassword().length() < 6 || user.getPassword().length() > 50
                || user.getRole() == null
                || (!user.getRole().trim().equalsIgnoreCase("ROLE_ADMIN")
                && !user.getRole().trim().equalsIgnoreCase("ROLE_USER"))) {
            return null;
        }
        if (user.hasDuplicatedEmail(userId)) {
            return new User();
        }
        user.setId(userId);
        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim());
        String salt = user.getId();
        String hashedPassword = passwordEncoder.encode(user.getPassword() + salt);
        user.setPassword(hashedPassword);
        user.setRole(user.getRole().trim().toUpperCase());
        return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUser(String userId) {
        List<Product> products = productRepository.findByUserId(userId);
        for (Product product: products) {
            productRepository.deleteById(product.getId());
        }
        userRepository.deleteById(userId);
        return "Deletion of user with id " + userId + " (and all of their products if any) successfully";
    }
}
