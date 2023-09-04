package com.LetsPlay.controller;

import com.LetsPlay.service.ProductService;
import com.LetsPlay.model.Product;
import com.LetsPlay.response.Response;
import com.LetsPlay.service.RateLimitService;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RateLimitService rateLimitService;

    @PermitAll
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        List<Product> products = productService.getAllProducts();
        if (!products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.convertToDtos(products));
        }
        Response errorResponse = new Response("No products exist in the system yet");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PermitAll
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.convertToDto(product.get()));
        }
        Response errorResponse = new Response("Product with id " + productId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        try {
            Product createdProduct = productService.createProduct(product);
            if (createdProduct == null) {
                Response errorResponse = new Response("Creation of new product failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " 'price' must be positive and not exceed 1000000000," +
                        " 'userId' must be a valid id of an existing user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.convertToDto(createdProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestBody Product product) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        if (!productService.findProductById(productId)) {
            Response errorResponse = new Response("Product with id " + productId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            if (updatedProduct == null) {
                Response errorResponse = new Response("Update of product with id "
                        + productId + " failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " 'price' must be positive and not exceed 1000000000," +
                        " 'userId' must be a valid id of an existing user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.OK).body(productService.convertToDto(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String productId) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        if (!productService.findProductById(productId)) {
            Response errorResponse = new Response("Product with id " + productId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            Response okResponse = new Response(productService.deleteProduct(productId));
            return ResponseEntity.status(HttpStatus.OK).body(okResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
