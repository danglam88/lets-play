package com.gritlab.service;

import com.gritlab.model.Product;
import com.gritlab.model.ProductDTO;
import com.gritlab.model.ProductRequest;
import com.gritlab.model.User;
import com.gritlab.repository.ProductRepository;
import com.gritlab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Product convertFromDto(ProductDTO productDTO) {
        return new Product(productDTO.getId(), productDTO.getName(), productDTO.getDescription(),
                productDTO.getPrice(), productDTO.getUserId());
    }

    public ProductDTO convertToDto(Product product) {
        ProductDTO productDTO = new ProductDTO(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getUserId(), null);
        Optional<User> user = userRepository.findById(product.getUserId());
        if (user.isPresent()) {
            productDTO.setOwner(user.get().getName());
        }
        return productDTO;
    }

    public List<ProductDTO> convertToDtos(List<Product> products) {
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDto)
                .toList();
        productDTOs.forEach(productDTO -> productDTO.setId(null));
        return productDTOs;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new NoSuchElementException("Product not found");
        }
        return product.get();
    }

    public Product createProduct(ProductRequest productRequest) {
        ProductDTO productDTO = new ProductDTO(null,
                productRequest.getName().replaceAll("\\s+", " ").trim(),
                productRequest.getDescription().replaceAll("\\s+", " ").trim(),
                productRequest.getPrice(),
                productRequest.getUserId(), null);

        String productId;
        do {
            productId = UUID.randomUUID().toString().split("-")[0];
        } while (productRepository.existsById(productId));

        productDTO = ProductDTO.builder()
                .id(productId)
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .userId(productDTO.getUserId())
                .build();

        return productRepository.save(convertFromDto(productDTO));
    }

    public Product updateProduct(Product product, ProductRequest productRequest) {
        ProductDTO productDTO = new ProductDTO(null,
                productRequest.getName().replaceAll("\\s+", " ").trim(),
                productRequest.getDescription().replaceAll("\\s+", " ").trim(),
                productRequest.getPrice(),
                productRequest.getUserId(), null);

        Optional<User> user = userRepository.findById(productRequest.getUserId());
        if (user.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }

        productDTO = ProductDTO.builder()
                .id(product.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .userId(productDTO.getUserId())
                .build();

        return productRepository.save(convertFromDto(productDTO));
    }

    public void deleteProduct(Product product) {
        productRepository.deleteById(product.getId());
    }
}
