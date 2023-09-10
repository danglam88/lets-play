package com.LetsPlay.controller;

import com.LetsPlay.model.User;
import com.LetsPlay.service.JwtService;
import com.LetsPlay.service.ProductService;
import com.LetsPlay.model.Product;
import com.LetsPlay.model.Response;
import com.LetsPlay.service.RateLimitService;
import com.LetsPlay.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
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
    private JwtService jwtService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimitService rateLimitService;

    @PermitAll
    @GetMapping
    public ResponseEntity<?> getAllProducts(HttpServletRequest request) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            Response okResponse = new Response("No products exist in the system yet");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(okResponse);
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.convertToDtos(products));
        }
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Optional<User> user = userService.getUserByEmail(username);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.convertToDtos(products));
        }
        if (user.get().getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.convertToNoUserIds(products));
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable String productId, HttpServletRequest request) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        Optional<Product> product = productService.getProductById(productId);
        if (!product.isPresent()) {
            Response errorResponse = new Response("Product with id " + productId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Response errorResponse = new Response("Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Optional<User> user = userService.getUserByEmail(username);
        if (!user.isPresent()) {
            Response errorResponse = new Response("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        if (user.get().getRole().equals("ROLE_USER")) {
            return ResponseEntity.status(HttpStatus.OK).body(productService.convertToNoUserId(product.get()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(product.get());
    }

    @Secured("ROLE_ADMIN")
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
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
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
            return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
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
