package com.LetsPlay.service;

import com.LetsPlay.repository.ProductRepository;
import com.LetsPlay.model.Product;
import com.LetsPlay.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public Product createProduct(Product product) {
        if (product.getName() == null
                || product.getName().trim().isEmpty() || product.getName().trim().length() > 50
                || product.getDescription() == null
                || product.getDescription().trim().isEmpty() || product.getDescription().trim().length() > 50
                || product.getPrice() == null
                || product.getPrice().isNaN() || product.getPrice() <= 0 || product.getPrice() > 1000000000
                || product.getUserId() == null
                || !userRepository.existsById(product.getUserId())) {
            return null;
        }
        String productId = "";
        do {
            productId = UUID.randomUUID().toString().split("-")[0];
        } while (productRepository.existsById(productId));
        product.setId(productId);
        product.setName(product.getName().trim());
        product.setDescription(product.getDescription().trim());
        return productRepository.save(product);
    }

    public boolean findProductById(String productId) {
        return productRepository.existsById(productId);
    }

    public Product updateProduct(String productId, Product product) {
        if (product.getName() == null
                || product.getName().trim().isEmpty() || product.getName().trim().length() > 50
                || product.getDescription() == null
                || product.getDescription().trim().isEmpty() || product.getDescription().trim().length() > 50
                || product.getPrice() == null
                || product.getPrice().isNaN() || product.getPrice() <= 0 || product.getPrice() > 1000000000
                || product.getUserId() == null
                || !userRepository.existsById(product.getUserId())) {
            return null;
        }
        product.setId(productId);
        product.setName(product.getName().trim());
        product.setDescription(product.getDescription().trim());
        return productRepository.save(product);
    }

    public String deleteProduct(String productId) {
        productRepository.deleteById(productId);
        return "Delete of product with id " + productId + " successfully";
    }
}
