package com.LetsPlay.controller;

import com.LetsPlay.model.RegRequest;
import com.LetsPlay.model.User;
import com.LetsPlay.model.Response;
import com.LetsPlay.service.RateLimitService;
import com.LetsPlay.service.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reg")
@AllArgsConstructor
public class RegController {

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimitService rateLimitService;

    @PermitAll
    @PostMapping
    public ResponseEntity<?> registerNewAccount(@RequestBody RegRequest regRequest) {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        try {
            User.fetchAllUsers(userService.getAllUsers());
            User createdAccount = userService.createAccount(regRequest);
            if (createdAccount == null) {
                Response errorResponse = new Response("Creation of new account failed:" +
                        " no any field can be empty (or contain only spaces)," +
                        " every field must have at most 50 characters," +
                        " 'email' must be in a correct format," +
                        " 'password' must have at least 6 characters");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            if (createdAccount.getId() == null) {
                Response errorResponse = new Response("Creation of new account failed due to" +
                        " duplicated email with an existing account");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.convertToNoPass(createdAccount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
