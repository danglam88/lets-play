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
        if (userRepository.findByRole("ROLE_ADMIN").isEmpty()) {
            String adminId = "";
            do {
                adminId = UUID.randomUUID().toString().split("-")[0];
            } while (userRepository.existsById(adminId));
            User admin = new User(adminId,
                    "Admin", "", "", "ROLE_ADMIN");
            do {
                admin.generateRandomEmail();
            } while (!admin.hasValidEmail() || admin.hasDuplicatedEmail(null));
            String salt = admin.getId();
            String hashedPassword = passwordEncoder.encode("Admin123" + salt);
            admin.setPassword(hashedPassword);
            userRepository.save(admin);
            String productId = "";
            do {
                productId = UUID.randomUUID().toString().split("-")[0];
            } while (productRepository.existsById(productId));
            Product product = new Product(productId,
                    "iPhone 14", "Apple iPhone 14 Pro Max", 1329.69, admin.getId());
            productRepository.save(product);
        }
    }
}
