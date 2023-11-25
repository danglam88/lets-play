package com.gritlab.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gritlab.model.UserRequest;
import com.gritlab.service.UserService;
import com.gritlab.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<JsonNode> getAllUsers(Authentication authentication) throws JsonProcessingException {
        User user = userService.authorizeUser(authentication, null);
        ObjectMapper objectMapper = new ObjectMapper();
        String usersNoPass = objectMapper.writeValueAsString(userService.convertToDtos(userService.getAllUsers()));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.readTree(usersNoPass));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/userInfo")
    public ResponseEntity<JsonNode> getUserInfo(Authentication authentication) throws JsonProcessingException {
        User user = userService.authorizeUser(authentication, null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userNoPass = objectMapper.writeValueAsString(userService.convertToDto(user));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.readTree(userNoPass));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<JsonNode> getUserById(@PathVariable String userId, Authentication authentication)
            throws JsonProcessingException {
        User user = userService.authorizeUser(authentication, userId);
        ObjectMapper objectMapper = new ObjectMapper();
        String userNoPass = objectMapper.writeValueAsString(userService.convertToDto(user));
        return ResponseEntity.status(HttpStatus.OK).body(objectMapper.readTree(userNoPass));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable String userId,
                                           @Valid @RequestBody UserRequest userRequest,
            BindingResult result,
            UriComponentsBuilder ucb, Authentication authentication) throws MethodArgumentNotValidException {

        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException((MethodParameter) null, result);
        }

        userService.authorizeUser(authentication, userId);
        User updatedUser = userService.updateUser(userId, userRequest);
        URI locationOfUpdatedUser = ucb
                .path("/users/{userId}")
                .buildAndExpand(updatedUser.getId())
                .toUri();
        return ResponseEntity.ok().location(locationOfUpdatedUser).build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId, Authentication authentication) {
        userService.authorizeUser(authentication, userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
