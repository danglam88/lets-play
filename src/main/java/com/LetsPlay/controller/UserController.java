package com.LetsPlay.controller;

import com.LetsPlay.model.AuthRequest;
import com.LetsPlay.response.ErrorResponse;
import com.LetsPlay.service.JwtService;
import com.LetsPlay.service.UserService;
import com.LetsPlay.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.size() > 0) {
            return ResponseEntity.ok(userService.convertToDtos(users));
        }
        ErrorResponse errorResponse = new ErrorResponse("No users exist in the system yet");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(userService.convertToDto(user.get()));
        }
        ErrorResponse errorResponse = new ErrorResponse("User with id " + userId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User.fetchAllUsers(userService.getAllUsers());
            User createdUser = userService.createUser(user);
            if (createdUser == null) {
                ErrorResponse errorResponse = new ErrorResponse("Creation of new user failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " email must be in a correct format," +
                        " password must have at least 6 characters");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (createdUser.getId() == null) {
                ErrorResponse errorResponse = new ErrorResponse("Creation of new user failed due to duplicated email");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.convertToDto(createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody User user) {
        if (!userService.findUserById(userId)) {
            ErrorResponse errorResponse = new ErrorResponse("User with id " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            User.fetchAllUsers(userService.getAllUsers());
            User updatedUser = userService.updateUser(userId, user);
            if (updatedUser == null) {
                ErrorResponse errorResponse = new ErrorResponse("Update of user with id " + userId + " failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " email must be in a correct format," +
                        " password must have at least 6 characters");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (updatedUser.getId() == null) {
                ErrorResponse errorResponse = new ErrorResponse("Update of user with id "
                        + userId + " failed due to duplicated email");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.ok(userService.convertToDto(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        if (!userService.findUserById(userId)) {
            ErrorResponse errorResponse = new ErrorResponse("User with id " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            String status = userService.deleteUser(userId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/auth")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }
}
