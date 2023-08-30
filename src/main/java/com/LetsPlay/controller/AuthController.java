package com.LetsPlay.controller;

import com.LetsPlay.model.AuthRequest;
import com.LetsPlay.model.User;
import com.LetsPlay.repository.UserRepository;
import com.LetsPlay.response.Response;
import com.LetsPlay.service.JwtService;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @PermitAll
    @PostMapping
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Optional<User> user = userRepository.findByEmail(authRequest.getUsername());
        if (user.isPresent()) {
            String salt = user.get().getId();
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword() + salt));
            if (authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.OK).body(jwtService.generateToken(authRequest.getUsername()));
            } else {
                Response errorResponse = new Response("Invalid authentication request");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
        } else {
            Response errorResponse = new Response("Invalid authentication request");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }
}
