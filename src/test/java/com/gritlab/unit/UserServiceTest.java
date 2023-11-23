package com.gritlab.unit;

import com.gritlab.model.*;
import com.gritlab.repository.UserRepository;
import com.gritlab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createAccountWhenValidDataThenReturnsUser() throws IOException {
        // Request params
        UserRequest userRequest = new UserRequest("Test Name",
                "test@mail.com", "Test1@", "SELLER");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // Act
        User result = userService.createAccount(userRequest);

        assertEquals(userRequest.getName(), result.getName());
        assertEquals(userRequest.getEmail(), result.getEmail());
        assertEquals(passwordEncoder.encode(
                userRequest.getPassword() + result.getId()), result.getPassword());
        assertEquals(userRequest.getRole(), result.getRole().toString());
    }

    @Test
    void updateUserWhenRequestIsValidThenReturnsUser() {
        // Create a sample User id
        String userId = "user-id1";

        // Request params
        UserRequest userRequest = new UserRequest("Test2 Name",
                "test2@mail.com", "Test2@", "SELLER");

        User user = new User(userId, "Test Name", "test@mail.com",
                "Test1@", Role.ADMIN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // Act
        User result = userService.updateUser(userId, userRequest);

        assertEquals(userRequest.getName(), result.getName());
        assertEquals(userRequest.getEmail(), result.getEmail());
        assertEquals(passwordEncoder.encode(
                userRequest.getPassword() + userId), result.getPassword());
        assertEquals(userRequest.getRole(), result.getRole().toString());
    }

    @Test
    void authorizeUserWhenValidDataThenReturnsUser() {
        // Create a sample User id
        String userId = "user-id1";

        when(userService.findUserById(userId)).thenReturn(true);

        User user = new User(userId, "Test Name", "test@mail.com",
                "Test1@", Role.ADMIN);

        // Mock UserDetails
        UserInfoUserDetails userDetails = mock(UserInfoUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userService.getUserByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

        // Act
        User result = userService.authorizeUser(authentication, userId);
        assertEquals(userId, result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
    }

    @Test
    void authorizeUserWhenNotFoundThenThrowEx() {
        // Create a sample User id
        String userId = "user-id1";

        when(userService.findUserById(userId)).thenReturn(false);

        Throwable exception = assertThrows(NoSuchElementException.class, () -> {
            userService.authorizeUser(authentication, userId);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void authorizeUserWhenSomeoneElseThenThrowEx() {
        // Create a sample User id
        String userId = "user-id1";

        when(userService.findUserById(userId)).thenReturn(true);

        User user = new User("user-id2", "Test Name", "test@mail.com",
                "Test1@", Role.ADMIN);

        // Mock UserDetails
        UserInfoUserDetails userDetails = mock(UserInfoUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userService.getUserByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(BadCredentialsException.class, () -> {
            userService.authorizeUser(authentication, userId);
        });

        assertEquals("Operation is not allowed", exception.getMessage());
    }
}
