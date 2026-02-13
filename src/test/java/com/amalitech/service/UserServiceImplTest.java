package com.amalitech.service;

import com.amalitech.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize test user
        testUser = new User();
        testUser.setId("507f1f77bcf86cd799439011");
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setAge(30);
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUserSuccess() {
        // Arrange
        User userToCreate = new User("Jane Smith", "jane.smith@example.com", 25);
        when(mongoTemplate.save(any(User.class))).thenReturn(testUser);

        // Act
        User createdUser = userService.createUser(userToCreate);

        // Assert
        assertNotNull(createdUser, "Created user should not be null");
        assertEquals("John Doe", createdUser.getName(), "User name should match");
        assertEquals("john.doe@example.com", createdUser.getEmail(), "User email should match");
        assertEquals(30, createdUser.getAge(), "User age should match");
        assertEquals("507f1f77bcf86cd799439011", createdUser.getId(), "User ID should be set");
        
        // Verify mongoTemplate.save was called once
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should save user with all fields")
    void testCreateUserWithAllFields() {
        // Arrange
        User userToCreate = new User("Alice Johnson", "alice@example.com", 28);
        User savedUser = new User("Alice Johnson", "alice@example.com", 28);
        savedUser.setId("507f1f77bcf86cd799439012");
        
        when(mongoTemplate.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(userToCreate);

        // Assert
        assertNotNull(result.getId(), "Saved user should have an ID");
        assertEquals("Alice Johnson", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals(28, result.getAge());
        
        verify(mongoTemplate, times(1)).save(userToCreate);
    }

    @Test
    @DisplayName("Should call mongoTemplate.save with correct user object")
    void testCreateUserCallsMongoTemplateSave() {
        // Arrange
        User userToCreate = new User("Bob Wilson", "bob@example.com", 35);
        when(mongoTemplate.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.createUser(userToCreate);

        // Assert
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle user creation with minimum required fields")
    void testCreateUserWithMinimumFields() {
        // Arrange
        User minimalUser = new User("TestUser", "test@example.com", 20);
        User savedUser = new User("TestUser", "test@example.com", 20);
        savedUser.setId("507f1f77bcf86cd799439013");
        
        when(mongoTemplate.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(minimalUser);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("TestUser", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(20, result.getAge());
    }

    @Test
    @DisplayName("Should return user with generated ID after creation")
    void testCreateUserReturnsUserWithId() {
        // Arrange
        User newUser = new User("Sarah Connor", "sarah@example.com", 32);
        User savedUserWithId = new User("Sarah Connor", "sarah@example.com", 32);
        savedUserWithId.setId("507f1f77bcf86cd799439014");
        
        when(mongoTemplate.save(any(User.class))).thenReturn(savedUserWithId);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result.getId(), "Result should have an ID");
        assertTrue(result.getId().length() > 0, "ID should not be empty");
    }

    @Test
    @DisplayName("Should verify mongoTemplate is used for persistence")
    void testMongoTemplatePersistence() {
        // Arrange
        User userToSave = new User("Tom Hardy", "tom@example.com", 45);
        when(mongoTemplate.save(userToSave)).thenReturn(testUser);

        // Act
        userService.createUser(userToSave);

        // Assert
        verify(mongoTemplate).save(userToSave);
        verifyNoMoreInteractions(mongoTemplate);
    }

    // US-002: Get All Users Tests
    
    @Test
    @DisplayName("Should return all users from database")
    void testGetAllUsersSuccess() {
        // Arrange
        User user1 = new User("User One", "user1@example.com", 25);
        user1.setId("1");
        User user2 = new User("User Two", "user2@example.com", 30);
        user2.setId("2");
        User user3 = new User("User Three", "user3@example.com", 35);
        user3.setId("3");
        
        List<User> expectedUsers = Arrays.asList(user1, user2, user3);
        when(mongoTemplate.findAll(User.class)).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertNotNull(actualUsers, "User list should not be null");
        assertEquals(3, actualUsers.size(), "Should return 3 users");
        assertEquals("User One", actualUsers.get(0).getName());
        assertEquals("User Two", actualUsers.get(1).getName());
        assertEquals("User Three", actualUsers.get(2).getName());
        verify(mongoTemplate, times(1)).findAll(User.class);
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmptyList() {
        // Arrange
        when(mongoTemplate.findAll(User.class)).thenReturn(Arrays.asList());

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertNotNull(actualUsers, "User list should not be null");
        assertEquals(0, actualUsers.size(), "Should return empty list");
        verify(mongoTemplate, times(1)).findAll(User.class);
    }

    @Test
    @DisplayName("Should call mongoTemplate.findAll with correct class")
    void testGetAllUsersCallsMongoTemplate() {
        // Arrange
        when(mongoTemplate.findAll(User.class)).thenReturn(Arrays.asList());

        // Act
        userService.getAllUsers();

        // Assert
        verify(mongoTemplate, times(1)).findAll(User.class);
    }

    // US-003: Get User by ID Tests
    
    @Test
    @DisplayName("Should return user when found by ID")
    void testGetUserByIdSuccess() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);

        // Act
        java.util.Optional<User> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent(), "User should be present");
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getName(), result.get().getName());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(mongoTemplate, times(1)).findById(userId, User.class);
    }

    @Test
    @DisplayName("Should return empty Optional when user not found")
    void testGetUserByIdNotFound() {
        // Arrange
        String userId = "nonexistent123";
        when(mongoTemplate.findById(userId, User.class)).thenReturn(null);

        // Act
        java.util.Optional<User> result = userService.getUserById(userId);

        // Assert
        assertFalse(result.isPresent(), "User should not be present");
        verify(mongoTemplate, times(1)).findById(userId, User.class);
    }

    @Test
    @DisplayName("Should call mongoTemplate.findById with correct parameters")
    void testGetUserByIdCallsMongoTemplate() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);

        // Act
        userService.getUserById(userId);

        // Assert
        verify(mongoTemplate, times(1)).findById(userId, User.class);
    }

    // US-004: Update User Tests
    
    @Test
    @DisplayName("Should update user successfully when user exists")
    void testUpdateUserSuccess() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("Jane Updated", "jane.updated@example.com", 28);
        
        User updatedUser = new User("Jane Updated", "jane.updated@example.com", 28);
        updatedUser.setId(userId);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);
        when(mongoTemplate.save(any(User.class))).thenReturn(updatedUser);

        // Act
        java.util.Optional<User> result = userService.updateUser(userId, updateData);

        // Assert
        assertTrue(result.isPresent(), "Updated user should be present");
        assertEquals("Jane Updated", result.get().getName());
        assertEquals("jane.updated@example.com", result.get().getEmail());
        assertEquals(28, result.get().getAge());
        verify(mongoTemplate, times(1)).findById(userId, User.class);
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return empty Optional when user not found for update")
    void testUpdateUserNotFound() {
        // Arrange
        String userId = "nonexistent123";
        User updateData = new User("Jane Updated", "jane.updated@example.com", 28);
        when(mongoTemplate.findById(userId, User.class)).thenReturn(null);

        // Act
        java.util.Optional<User> result = userService.updateUser(userId, updateData);

        // Assert
        assertFalse(result.isPresent(), "User should not be present");
        verify(mongoTemplate, times(1)).findById(userId, User.class);
        verify(mongoTemplate, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update all user fields")
    void testUpdateUserAllFields() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("New Name", "new.email@example.com", 50);
        
        User existingUser = new User("Old Name", "old.email@example.com", 30);
        existingUser.setId(userId);
        
        User savedUser = new User("New Name", "new.email@example.com", 50);
        savedUser.setId(userId);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        when(mongoTemplate.save(any(User.class))).thenReturn(savedUser);

        // Act
        java.util.Optional<User> result = userService.updateUser(userId, updateData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getName());
        assertEquals("new.email@example.com", result.get().getEmail());
        assertEquals(50, result.get().getAge());
        assertEquals(userId, result.get().getId());
    }

    @Test
    @DisplayName("Should preserve user ID when updating")
    void testUpdateUserPreservesId() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("Updated Name", "updated@example.com", 35);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);
        when(mongoTemplate.save(any(User.class))).thenReturn(testUser);

        // Act
        java.util.Optional<User> result = userService.updateUser(userId, updateData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
    }

    @Test
    @DisplayName("Should call mongoTemplate save with updated user")
    void testUpdateUserCallsSave() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("Updated", "updated@example.com", 40);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);
        when(mongoTemplate.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.updateUser(userId, updateData);

        // Assert
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

}