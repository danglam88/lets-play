package com.LetsPlay.controller;

import com.LetsPlay.response.ErrorResponse;
import com.LetsPlay.service.ProductService;
import com.LetsPlay.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.size() > 0) {
            return ResponseEntity.ok(products);
        }
        ErrorResponse errorResponse = new ErrorResponse("No products exist in the system yet");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        }
        ErrorResponse errorResponse = new ErrorResponse("Product with id " + productId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            if (createdProduct == null) {
                ErrorResponse errorResponse = new ErrorResponse("Creation of new product failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " price must be positive and not exceed 1000000000");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestBody Product product) {
        if (!productService.findProductById(productId)) {
            ErrorResponse errorResponse = new ErrorResponse("Product with id " + productId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            if (updatedProduct == null) {
                ErrorResponse errorResponse = new ErrorResponse("Update of product with id "
                        + productId + " failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " price must be positive and not exceed 1000000000");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        if (!productService.findProductById(productId)) {
            ErrorResponse errorResponse = new ErrorResponse("Product with id " + productId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            String status = productService.deleteProduct(productId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
