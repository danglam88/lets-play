package com.gritlab.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gritlab.controller.UserController;
import com.gritlab.model.User;
import com.gritlab.model.UserDTO;
import com.gritlab.model.UserRequest;
import com.gritlab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Autowired
    private UriComponentsBuilder ucb;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userController = new UserController(userService);
        ucb = UriComponentsBuilder.newInstance();
    }

    @Test
    void testGetUserInfo() throws JsonProcessingException {
        // Create a sample User id
        String userId = "user-id1";

        User authorizedUser = new User(userId, "Test Name", "test@mail.com",
                "Test1@", "ADMIN");

        when(userService.authorizeUser(authentication, null)).thenReturn(authorizedUser);

        ObjectMapper objectMapper = new ObjectMapper();

        // Mock the userService's getUserInfo method data
        UserDTO userDTO = new UserDTO(userId, "Test Name", "test@mail.com",
                "Test1@", "ADMIN");

        when(userService.convertToDto(authorizedUser)).thenReturn(userDTO);

        // Call the controller method
        ResponseEntity<JsonNode> response = userController.getUserInfo(authentication);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        String expectedJson = objectMapper.writeValueAsString(userDTO);
        String actualJson = objectMapper.writeValueAsString(response.getBody());
        assertEquals(expectedJson, actualJson);
    }

    @Test
    void testGetAvatarById() throws IOException {
        // Create a sample User id
        String userId = "user-id1";

        User authorizedUser = new User(userId, "Test Name", "test@mail.com",
                "Test1@", "ADMIN");

        when(userService.authorizeUser(authentication, userId)).thenReturn(authorizedUser);
    }

    @Test
    void testGetUserById() throws JsonProcessingException {
        // Create a sample User id
        String userId = "user-id1";

        User authorizedUser = new User(userId, "Test Name", "test@mail.com",
                "Test1@", "ADMIN");

        when(userService.authorizeUser(authentication, userId)).thenReturn(authorizedUser);

        ObjectMapper objectMapper = new ObjectMapper();

        // Mock the userService's getUserById method data
        UserDTO userDTO = new UserDTO(userId, "Test Name", "test@mail.com",
                "Test1@", "ADMIN");
        when(userService.convertToDto(authorizedUser)).thenReturn(userDTO);

        // Call the controller method
        ResponseEntity<JsonNode> response = userController.getUserById(userId, authentication);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        String expectedJson = objectMapper.writeValueAsString(userDTO);
        String actualJson = objectMapper.writeValueAsString(response.getBody());
        assertEquals(expectedJson, actualJson);
    }

    @Test
    void updateUserWhenValidInputThenReturns200() throws MethodArgumentNotValidException {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        // Create a sample User id
        String userId = "user-id1";

        User authorizedUser = new User(userId, "Test Name", "test@mail.com",
                "Test1@", "ADMIN");

        when(userService.authorizeUser(authentication, userId)).thenReturn(authorizedUser);

        // Request params
        UserRequest userRequest = new UserRequest("Test2 Name",
                "test2@mail.com", "Test2@", "SELLER");

        // Mock the userService's updateUser method data
        User updatedUser = new User(userId, "Test2 Name", "test2@mail.com",
                "Test2@", "ADMIN");

        // Update an existing user as needed
        when(userService.updateUser(userId, userRequest)).thenReturn(updatedUser);

        // Call the controller method
        ResponseEntity<Void> responseEntity = userController.updateUser(userId, userRequest,
                bindingResult, ucb, authentication);

        // Assertions
        assertNotNull(responseEntity);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("/users/" + userId, responseEntity.getHeaders().get("Location").get(0));
    }

    @Test
    void deleteUserWhenValidInputThenReturns200() throws Exception {
        // Create a sample User id
        String userId = "user-id1";

        User authorizedUser = new User(userId, "Test Name", "test@mail.com",
                "Test1@", "ADMIN");

        when(userService.authorizeUser(authentication, userId)).thenReturn(authorizedUser);

        // Call the controller method
        ResponseEntity<Void> responseEntity = userController.deleteUser(userId, authentication);

        // Assertions
        assertNotNull(responseEntity);

        assertEquals(200, responseEntity.getStatusCodeValue());
    }
}
