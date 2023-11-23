package com.gritlab.controller;

import com.gritlab.model.User;
import com.gritlab.model.UserRequest;
import com.gritlab.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/reg")
@AllArgsConstructor
public class RegController {

    @Autowired
    private UserService userService;

    @PermitAll
    @PostMapping
    public ResponseEntity<Void> registerNewAccount(@Valid @RequestBody UserRequest userRequest,
                                                BindingResult result,
                                                UriComponentsBuilder ucb) throws MethodArgumentNotValidException {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException((MethodParameter) null, result);
        }

        User createdAccount = userService.createAccount(userRequest);
        URI locationOfNewUser = ucb
                .path("/users/{userId}")
                .buildAndExpand(createdAccount.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewUser).build();
    }
}
