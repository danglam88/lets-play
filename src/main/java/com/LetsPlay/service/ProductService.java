package com.LetsPlay.service;

import com.LetsPlay.model.ProductDTO;
import com.LetsPlay.model.ProductNoUserId;
import com.LetsPlay.model.User;
import com.LetsPlay.repository.ProductRepository;
import com.LetsPlay.model.Product;
import com.LetsPlay.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ProductDTO convertToDto(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        Optional<User> user = userRepository.findById(product.getUserId());
        if (user.isPresent()) {
            productDTO.setOwner(user.get().getName());
        }
        return productDTO;
    }

    public List<ProductDTO> convertToDtos(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ProductNoUserId convertToNoUserId(Product product) {
        ProductNoUserId productNoUserId = modelMapper.map(product, ProductNoUserId.class);
        Optional<User> user = userRepository.findById(product.getUserId());
        if (user.isPresent()) {
            productNoUserId.setOwner(user.get().getName());
        }
        return productNoUserId;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<ProductNoUserId> convertToNoUserIds(List<Product> products) {
        return products.stream()
                .map(this::convertToNoUserId)
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public List<Product> getProductsByUserEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            return new ArrayList<>();
        }
        return productRepository.findByUserId(user.get().getId());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public boolean findProductById(String productId) {
        return productRepository.existsById(productId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteProduct(String productId) {
        productRepository.deleteById(productId);
        return "Deletion of product with id " + productId + " successfully";
    }
}
