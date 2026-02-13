package com.amalitech.controller;

import com.amalitech.exception.DuplicateEmailException;
import com.amalitech.exception.GlobalExceptionHandler;
import com.amalitech.model.User;
import com.amalitech.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        // Initialize test user
        testUser = new User();
        testUser.setId("507f1f77bcf86cd799439011");
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setAge(30);
    }

    @Test
    @DisplayName("Should create user and return 201 Created")
    void testCreateUserSuccess() throws Exception {
        // Arrange
        User newUser = new User("Jane Smith", "jane.smith@example.com", 25);
        User createdUser = new User("Jane Smith", "jane.smith@example.com", 25);
        createdUser.setId("507f1f77bcf86cd799439012");

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("507f1f77bcf86cd799439012")))
                .andExpect(jsonPath("$.name", is("Jane Smith")))
                .andExpect(jsonPath("$.email", is("jane.smith@example.com")))
                .andExpect(jsonPath("$.age", is(25)));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should call userService.createUser with correct data")
    void testCreateUserCallsService() throws Exception {
        // Arrange
        User newUser = new User("Alice Johnson", "alice@example.com", 28);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());

        // Assert
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should return created user with all fields")
    void testCreateUserReturnsCompleteUser() throws Exception {
        // Arrange
        User inputUser = new User("Bob Wilson", "bob@example.com", 35);
        User outputUser = new User("Bob Wilson", "bob@example.com", 35);
        outputUser.setId("507f1f77bcf86cd799439013");

        when(userService.createUser(any(User.class))).thenReturn(outputUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.age").exists());
    }

    @Test
    @DisplayName("Should handle POST request to correct endpoint")
    void testCreateUserEndpointMapping() throws Exception {
        // Arrange
        User newUser = new User("Test User", "test@example.com", 30);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());

        verify(userService).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should return user response with ID after creation")
    void testCreateUserResponseIncludesId() throws Exception {
        // Arrange
        User requestUser = new User("Sarah Connor", "sarah@example.com", 32);
        User responseUser = new User("Sarah Connor", "sarah@example.com", 32);
        responseUser.setId("507f1f77bcf86cd799439014");

        when(userService.createUser(any(User.class))).thenReturn(responseUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("Should accept JSON content type for user creation")
    void testCreateUserAcceptsJson() throws Exception {
        // Arrange
        User newUser = new User("Tom Hardy", "tom@example.com", 45);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return response with correct HTTP headers")
    void testCreateUserResponseHeaders() throws Exception {
        // Arrange
        User newUser = new User("Emma Stone", "emma@example.com", 26);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should properly serialize user object in response")
    void testCreateUserResponseSerialization() throws Exception {
        // Arrange
        User inputUser = new User("Chris Hemsworth", "chris@example.com", 40);
        User outputUser = new User("Chris Hemsworth", "chris@example.com", 40);
        outputUser.setId("507f1f77bcf86cd799439015");

        when(userService.createUser(any(User.class))).thenReturn(outputUser);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Chris Hemsworth")))
                .andExpect(jsonPath("$.email", is("chris@example.com")))
                .andExpect(jsonPath("$.age", is(40)));
    }

    // US-002: Get All Users Tests
    
    @Test
    @DisplayName("Should return all users and status 200 OK")
    void testGetAllUsersSuccess() throws Exception {
        // Arrange
        User user1 = new User("User One", "user1@example.com", 25);
        user1.setId("1");
        User user2 = new User("User Two", "user2@example.com", 30);
        user2.setId("2");
        User user3 = new User("User Three", "user3@example.com", 35);
        user3.setId("3");
        
        List<User> users = Arrays.asList(user1, user2, user3);
        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("User One")))
                .andExpect(jsonPath("$[1].name", is("User Two")))
                .andExpect(jsonPath("$[2].name", is("User Three")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmptyList() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should call userService.getAllUsers")
    void testGetAllUsersCallsService() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // Act
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());

        // Assert
        verify(userService, times(1)).getAllUsers();
    }

    // US-003: Get User by ID Tests
    
    @Test
    @DisplayName("Should return user and status 200 OK when user exists")
    void testGetUserByIdSuccess() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(userService.getUserById(userId)).thenReturn(java.util.Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.age", is(30)));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("Should return 404 Not Found when user doesn't exist")
    void testGetUserByIdNotFound() throws Exception {
        // Arrange
        String userId = "nonexistent123";
        when(userService.getUserById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("Should call userService.getUserById with correct ID")
    void testGetUserByIdCallsService() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(userService.getUserById(userId)).thenReturn(java.util.Optional.of(testUser));

        // Act
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk());

        // Assert
        verify(userService, times(1)).getUserById(userId);
    }

    // US-006: Input Validation Tests
    
    @Test
    @DisplayName("Should return 400 Bad Request when name is blank")
    void testCreateUserValidationBlankName() throws Exception {
        // Arrange
        User invalidUser = new User("", "test@example.com", 25);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.fieldErrors.name").exists());
        
        verify(userService, never()).createUser(any(User.class));
    }
    
    @Test
    @DisplayName("Should return 400 Bad Request when email format is invalid")
    void testCreateUserValidationInvalidEmail() throws Exception {
        // Arrange
        User invalidUser = new User("Test User", "invalid-email", 25);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email", is("Email must be a valid email address")));
        
        verify(userService, never()).createUser(any(User.class));
    }
    
    @Test
    @DisplayName("Should return 400 Bad Request when age is below minimum")
    void testCreateUserValidationAgeTooLow() throws Exception {
        // Arrange
        User invalidUser = new User("Test User", "test@example.com", 17);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.age", is("Age must be at least 18")));
        
        verify(userService, never()).createUser(any(User.class));
    }
    
    @Test
    @DisplayName("Should return 400 Bad Request when age exceeds maximum")
    void testCreateUserValidationAgeTooHigh() throws Exception {
        // Arrange
        User invalidUser = new User("Test User", "test@example.com", 101);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.age", is("Age must not exceed 100")));
        
        verify(userService, never()).createUser(any(User.class));
    }
    
    @Test
    @DisplayName("Should not call service when validation fails")
    void testCreateUserValidationDoesNotCallService() throws Exception {
        // Arrange
        User invalidUser = new User("", "invalid", 17);
        
        // Act
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
        
        // Assert
        verify(userService, never()).createUser(any(User.class));
    }

    // US-004: Update User Tests
    
    @Test
    @DisplayName("Should update user and return 200 OK when user exists")
    void testUpdateUserSuccess() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("Jane Updated", "jane.updated@example.com", 28);
        User updatedUser = new User("Jane Updated", "jane.updated@example.com", 28);
        updatedUser.setId(userId);
        
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(java.util.Optional.of(updatedUser));

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Jane Updated")))
                .andExpect(jsonPath("$.email", is("jane.updated@example.com")))
                .andExpect(jsonPath("$.age", is(28)));

        verify(userService, times(1)).updateUser(eq(userId), any(User.class));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating non-existent user")
    void testUpdateUserNotFound() throws Exception {
        // Arrange
        String userId = "nonexistent123";
        User updateData = new User("Jane Updated", "jane.updated@example.com", 28);
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(userId), any(User.class));
    }

    @Test
    @DisplayName("Should validate input when updating user")
    void testUpdateUserValidation() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User invalidUpdateData = new User("", "invalid-email", 17);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdateData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(userService, never()).updateUser(anyString(), any(User.class));
    }

    @Test
    @DisplayName("Should call userService.updateUser with correct parameters")
    void testUpdateUserCallsService() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("Updated Name", "updated@example.com", 30);
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(java.util.Optional.of(testUser));

        // Act
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        // Assert
        verify(userService, times(1)).updateUser(eq(userId), any(User.class));
    }

    @Test
    @DisplayName("Should return updated user with all fields")
    void testUpdateUserReturnsCompleteUser() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("Complete User", "complete@example.com", 45);
        User updatedUser = new User("Complete User", "complete@example.com", 45);
        updatedUser.setId(userId);
        
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(java.util.Optional.of(updatedUser));

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.age").exists());
    }

    // US-005: Delete User Tests
    
    @Test
    @DisplayName("Should delete user and return 204 No Content when user exists")
    void testDeleteUserSuccess() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(userService.deleteUser(userId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting non-existent user")
    void testDeleteUserNotFound() throws Exception {
        // Arrange
        String userId = "nonexistent123";
        when(userService.deleteUser(userId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should call userService.deleteUser with correct ID")
    void testDeleteUserCallsService() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(userService.deleteUser(userId)).thenReturn(true);

        // Act
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());

        // Assert
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should return empty body on successful deletion")
    void testDeleteUserReturnsEmptyBody() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(userService.deleteUser(userId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    // US-007: Unique Email Enforcement Tests

    @Test
    @DisplayName("Should return 409 Conflict when creating user with duplicate email")
    void testCreateUserDuplicateEmail() throws Exception {
        // Arrange
        User userToCreate = new User("Jane Smith", "john.doe@example.com", 25);
        String userJson = objectMapper.writeValueAsString(userToCreate);
        
        when(userService.createUser(any(User.class)))
            .thenThrow(new DuplicateEmailException("john.doe@example.com"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
    }

    @Test
    @DisplayName("Should return 409 Conflict when updating user with duplicate email")
    void testUpdateUserDuplicateEmail() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("John Updated", "existing@example.com", 35);
        String userJson = objectMapper.writeValueAsString(updateData);
        
        when(userService.updateUser(eq(userId), any(User.class)))
            .thenThrow(new DuplicateEmailException("existing@example.com"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("Email already exists")));
    }

    @Test
    @DisplayName("Should include email in error message for duplicate email on create")
    void testCreateUserDuplicateEmailIncludesEmail() throws Exception {
        // Arrange
        User userToCreate = new User("Jane Smith", "duplicate@example.com", 25);
        String userJson = objectMapper.writeValueAsString(userToCreate);
        
        when(userService.createUser(any(User.class)))
            .thenThrow(new DuplicateEmailException("duplicate@example.com"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Email already exists: duplicate@example.com")));
    }

    @Test
    @DisplayName("Should include email in error message for duplicate email on update")
    void testUpdateUserDuplicateEmailIncludesEmail() throws Exception {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("John Updated", "duplicate@example.com", 35);
        String userJson = objectMapper.writeValueAsString(updateData);
        
        when(userService.updateUser(eq(userId), any(User.class)))
            .thenThrow(new DuplicateEmailException("duplicate@example.com"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Email already exists: duplicate@example.com")));
    }

}