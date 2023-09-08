package com.LetsPlay.controller;

import com.LetsPlay.config.UserInfoUserDetails;
import com.LetsPlay.model.User;
import com.LetsPlay.response.Response;
import com.LetsPlay.service.RateLimitService;
import com.LetsPlay.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/ownUserInfo")
@AllArgsConstructor
public class OwnUserController {

    @Autowired
    private UserInfoUserDetails userInfoUserDetails;

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimitService rateLimitService;

    @Secured({ "ROLE_ADMIN", "ROLE_USER" })
    @GetMapping
    public ResponseEntity<?> getOwnUserInfo() {
        if (!rateLimitService.allowRequest()) {
            Response errorResponse = new Response("Too many requests, please try again later");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
        Optional<User> user = userService.getUserByEmail(userInfoUserDetails.getUsername());
        if (user.isPresent()) {
            User userInfo = user.get();
            userInfo.setPassword(null);
            return ResponseEntity.status(HttpStatus.OK).body(userInfo);
        }
        Response errorResponse = new Response("User with email " + userInfoUserDetails.getUsername() + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
