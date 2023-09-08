package com.LetsPlay.controller;

import com.LetsPlay.model.Product;
import com.LetsPlay.model.User;
import com.LetsPlay.response.Response;
import com.LetsPlay.service.JwtService;
import com.LetsPlay.service.ProductService;
import com.LetsPlay.service.RateLimitService;
import com.LetsPlay.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ownProductInfo")
@AllArgsConstructor
public class OwnProductController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RateLimitService rateLimitService;

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @GetMapping
    public ResponseEntity<?> getOwnProductInfo(HttpServletRequest request) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Response errorResponse = new Response("User is not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Optional<User> user = userService.getUserByEmail(username);
        if (!user.isPresent()) {
            Response errorResponse = new Response("User with email "
                    + username + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        List<Product> products = productService.getProductsByUserEmail(username);
        if (products.isEmpty()) {
            Response errorResponse = new Response("No products exist for user with email "
                    + username + " in the system yet");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }
}
