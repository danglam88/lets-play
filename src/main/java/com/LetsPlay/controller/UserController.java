package com.LetsPlay.controller;

import com.LetsPlay.service.JwtService;
import com.LetsPlay.service.RateLimitService;
import com.LetsPlay.service.UserService;
import com.LetsPlay.model.User;
import com.LetsPlay.model.Response;
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
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimitService rateLimitService;

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            Response errorResponse = new Response("No users exist in the system yet");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
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
            Response errorResponse = new Response("User with email " + username + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        if (user.get().getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.convertToNoPasses(users));
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.convertToDtos(users));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            Response errorResponse = new Response("User with id " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.convertToNoPass(user.get()));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        try {
            User.fetchAllUsers(userService.getAllUsers());
            User createdUser = userService.createUser(user);
            if (createdUser == null) {
                Response errorResponse = new Response("Creation of new user failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " 'email' must be in a correct format," +
                        " 'password' must have at least 6 characters," +
                        " 'role' must be either role_admin or role_user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (createdUser.getId() == null) {
                Response errorResponse = new Response("Creation of new user failed due to" +
                        " duplicated email with an existing user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.convertToNoPass(createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody User user) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        if (!userService.findUserById(userId)) {
            Response errorResponse = new Response("User with id " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            User.fetchAllUsers(userService.getAllUsers());
            User updatedUser = userService.updateUser(userId, user);
            if (updatedUser == null) {
                Response errorResponse = new Response("Update of user with id " + userId + " failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " 'email' must be in a correct format," +
                        " 'password' must have at least 6 characters," +
                        " 'role' must be either role_admin or role_user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (updatedUser.getId() == null) {
                Response errorResponse = new Response("Update of user with id "
                        + userId + " failed due to duplicated email with an existing user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.OK).body(userService.convertToNoPass(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        if (!userService.findUserById(userId)) {
            Response errorResponse = new Response("User with id " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        try {
            Response okResponse = new Response(userService.deleteUser(userId));
            return ResponseEntity.status(HttpStatus.OK).body(okResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
