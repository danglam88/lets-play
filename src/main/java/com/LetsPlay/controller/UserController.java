package com.LetsPlay.controller;

import com.LetsPlay.response.ErrorResponse;
import com.LetsPlay.service.UserService;
import com.LetsPlay.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.size() > 0) {
            return ResponseEntity.ok(users);
        }
        ErrorResponse errorResponse = new ErrorResponse("No users exist in the system yet");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        ErrorResponse errorResponse = new ErrorResponse("User with id " + userId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            if (createdUser == null) {
                ErrorResponse errorResponse = new ErrorResponse("Creation of new user failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Creation of new user failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody User user) {
        if (!userService.findUserById(userId)) {
            ErrorResponse errorResponse = new ErrorResponse("User with id " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            User updatedUser = userService.updateUser(userId, user);
            if (updatedUser == null) {
                ErrorResponse errorResponse = new ErrorResponse("Update of user with id " + userId + " failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Update of user with id " + userId + " failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        if (!userService.findUserById(userId)) {
            ErrorResponse errorResponse = new ErrorResponse("User with id " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            String status = userService.deleteUser(userId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Deletion of user with id " + userId + " failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
