package com.LetsPlay.service;

import com.LetsPlay.model.Product;
import com.LetsPlay.repository.ProductRepository;
import com.LetsPlay.repository.UserRepository;
import com.LetsPlay.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    //private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user: users) {
            user.setPassword(null);
        }
        return users;
    }

    public Optional<User> getUserById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        user.get().setPassword(null);
        return user;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()
                || user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.hasValidEmail()
                || user.getPassword() == null || user.getPassword().trim().isEmpty()
                || user.getRole() == null || user.getRole().trim().isEmpty()
                || (!user.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("user"))) {
            return null;
        }
        /*String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);*/
        user.setId(UUID.randomUUID().toString().split("-")[0]);
        user.setRole(user.getRole().toUpperCase());
        userRepository.save(user);
        user.setPassword(null);
        return user;
    }

    public boolean findUserById(String userId) {
        return userRepository.existsById(userId);
    }

    public User updateUser(String userId, User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()
                || user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.hasValidEmail()
                || user.getPassword() == null || user.getPassword().trim().isEmpty()
                || user.getRole() == null || user.getRole().trim().isEmpty()
                || (!user.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("user"))) {
            return null;
        }
        /*String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);*/
        user.setId(userId);
        user.setRole(user.getRole().toUpperCase());
        userRepository.save(user);
        user.setPassword(null);
        return user;
    }

    public String deleteUser(String userId) {
        List<Product> products = productRepository.findProductsByUserId(userId);
        for (Product product: products) {
            productRepository.deleteById(product.getId());
        }
        userRepository.deleteById(userId);
        return "Delete of user with id " + userId + " successful";
    }
}
