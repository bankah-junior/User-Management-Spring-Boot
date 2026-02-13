package com.amalitech.service;

import com.amalitech.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

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

}