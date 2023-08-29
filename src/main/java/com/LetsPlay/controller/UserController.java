package com.LetsPlay.controller;

import com.LetsPlay.service.UserService;
import com.LetsPlay.model.User;
import com.LetsPlay.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.convertToDtos(users));
        }
        Response errorResponse = new Response("No users exist in the system yet");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.convertToDto(user.get()));
        }
        Response errorResponse = new Response("User with id " + userId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User.fetchAllUsers(userService.getAllUsers());
            User createdUser = userService.createUser(user);
            if (createdUser == null) {
                Response errorResponse = new Response("Creation of new user failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " email must be in a correct format," +
                        " password must have at least 6 characters," +
                        " role must be either role_admin or role_user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (createdUser.getId() == null) {
                Response errorResponse = new Response("Creation of new user failed due to duplicated email");
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
                        " email must be in a correct format," +
                        " password must have at least 6 characters," +
                        " role must be either role_admin or role_user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (updatedUser.getId() == null) {
                Response errorResponse = new Response("Update of user with id "
                        + userId + " failed due to duplicated email");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.OK).body(userService.convertToDto(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
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
