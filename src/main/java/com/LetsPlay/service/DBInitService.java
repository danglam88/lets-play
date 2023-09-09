package com.LetsPlay.service;

import com.LetsPlay.model.Product;
import com.LetsPlay.model.User;
import com.LetsPlay.repository.ProductRepository;
import com.LetsPlay.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DBInitService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.findAll().isEmpty()) {
            User admin = new User(UUID.randomUUID().toString().split("-")[0],
                    "Admin", "admin@mail.com", "Admin123", "ROLE_ADMIN");
            String salt = admin.getId();
            String hashedPassword = passwordEncoder.encode(admin.getPassword() + salt);
            admin.setPassword(hashedPassword);
            userRepository.save(admin);
            Product product = new Product(UUID.randomUUID().toString().split("-")[0],
                    "iPhone 14", "Apple iPhone 14 Pro Max", 1329.69, admin.getId());
            productRepository.save(product);
        }
    }
}
