package com.amalitech.service;

import com.amalitech.exception.DuplicateEmailException;
import com.amalitech.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

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

    // US-005: Delete User Tests
    
    @Test
    @DisplayName("Should delete user successfully when user exists")
    void testDeleteUserSuccess() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);

        // Act
        boolean result = userService.deleteUser(userId);

        // Assert
        assertTrue(result, "Delete should return true when user exists");
        verify(mongoTemplate, times(1)).findById(userId, User.class);
        verify(mongoTemplate, times(1)).remove(any(org.springframework.data.mongodb.core.query.Query.class), eq(User.class));
    }

    @Test
    @DisplayName("Should return false when deleting non-existent user")
    void testDeleteUserNotFound() {
        // Arrange
        String userId = "nonexistent123";
        when(mongoTemplate.findById(userId, User.class)).thenReturn(null);

        // Act
        boolean result = userService.deleteUser(userId);

        // Assert
        assertFalse(result, "Delete should return false when user doesn't exist");
        verify(mongoTemplate, times(1)).findById(userId, User.class);
        verify(mongoTemplate, never()).remove(any(org.springframework.data.mongodb.core.query.Query.class), eq(User.class));
    }

    @Test
    @DisplayName("Should call mongoTemplate.remove when deleting user")
    void testDeleteUserCallsRemove() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(mongoTemplate, times(1)).remove(any(org.springframework.data.mongodb.core.query.Query.class), eq(User.class));
    }

    @Test
    @DisplayName("Should check if user exists before deleting")
    void testDeleteUserChecksExistence() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        when(mongoTemplate.findById(userId, User.class)).thenReturn(testUser);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(mongoTemplate, times(1)).findById(userId, User.class);
    }

    // US-007: Unique Email Enforcement Tests

    @Test
    @DisplayName("Should throw DuplicateEmailException when creating user with existing email")
    void testCreateUserWithDuplicateEmail() {
        // Arrange
        User userToCreate = new User("Jane Smith", "john.doe@example.com", 25);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(true);

        // Act & Assert
        DuplicateEmailException exception = assertThrows(
            DuplicateEmailException.class,
            () -> userService.createUser(userToCreate)
        );
        
        assertEquals("Email already exists: john.doe@example.com", exception.getMessage());
        verify(mongoTemplate, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should check email uniqueness before creating user")
    void testCreateUserChecksEmailUniqueness() {
        // Arrange
        User userToCreate = new User("Jane Smith", "jane@example.com", 25);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.createUser(userToCreate);

        // Assert
        verify(mongoTemplate, times(1)).exists(any(Query.class), eq(User.class));
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateEmailException when updating user with existing email")
    void testUpdateUserWithDuplicateEmail() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("John Updated", "existing@example.com", 35);
        
        User existingUser = new User("John Doe", "john.doe@example.com", 30);
        existingUser.setId(userId);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(true);

        // Act & Assert
        DuplicateEmailException exception = assertThrows(
            DuplicateEmailException.class,
            () -> userService.updateUser(userId, updateData)
        );
        
        assertEquals("Email already exists: existing@example.com", exception.getMessage());
        verify(mongoTemplate, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow updating user with same email")
    void testUpdateUserWithSameEmail() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("John Updated", "john.doe@example.com", 35);
        
        User existingUser = new User("John Doe", "john.doe@example.com", 30);
        existingUser.setId(userId);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        when(mongoTemplate.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateUser(userId, updateData);

        // Assert - Should not check email uniqueness when email hasn't changed
        verify(mongoTemplate, never()).exists(any(Query.class), eq(User.class));
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow updating user with new unique email")
    void testUpdateUserWithNewUniqueEmail() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User updateData = new User("John Updated", "new.email@example.com", 35);
        
        User existingUser = new User("John Doe", "john.doe@example.com", 30);
        existingUser.setId(userId);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(existingUser);

        // Act
        userService.updateUser(userId, updateData);

        // Assert
        verify(mongoTemplate, times(1)).exists(any(Query.class), eq(User.class));
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    // US-009: Comprehensive Unit Tests - Edge Cases and Boundary Values

    @Test
    @DisplayName("Should handle user with minimum valid age (18)")
    void testCreateUserWithMinimumAge() {
        // Arrange
        User userToCreate = new User("Young User", "young@example.com", 18);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(userToCreate);

        // Act
        User createdUser = userService.createUser(userToCreate);

        // Assert
        assertNotNull(createdUser);
        assertEquals(18, createdUser.getAge());
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle user with maximum valid age (100)")
    void testCreateUserWithMaximumAge() {
        // Arrange
        User userToCreate = new User("Senior User", "senior@example.com", 100);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(userToCreate);

        // Act
        User createdUser = userService.createUser(userToCreate);

        // Assert
        assertNotNull(createdUser);
        assertEquals(100, createdUser.getAge());
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle user with very long name")
    void testCreateUserWithLongName() {
        // Arrange
        String longName = "A".repeat(255);
        User userToCreate = new User(longName, "longname@example.com", 30);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(userToCreate);

        // Act
        User createdUser = userService.createUser(userToCreate);

        // Assert
        assertNotNull(createdUser);
        assertEquals(longName, createdUser.getName());
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle email with special characters")
    void testCreateUserWithSpecialCharactersEmail() {
        // Arrange
        User userToCreate = new User("Special User", "user+tag@sub-domain.example.com", 30);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(userToCreate);

        // Act
        User createdUser = userService.createUser(userToCreate);

        // Assert
        assertNotNull(createdUser);
        assertEquals("user+tag@sub-domain.example.com", createdUser.getEmail());
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle name with special characters")
    void testCreateUserWithSpecialCharactersName() {
        // Arrange
        User userToCreate = new User("José María O'Brien-Smith", "jose@example.com", 30);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(userToCreate);

        // Act
        User createdUser = userService.createUser(userToCreate);

        // Assert
        assertNotNull(createdUser);
        assertEquals("José María O'Brien-Smith", createdUser.getName());
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle multiple users with different emails")
    void testCreateMultipleUsersWithDifferentEmails() {
        // Arrange
        User user1 = new User("User One", "user1@example.com", 25);
        User user2 = new User("User Two", "user2@example.com", 30);
        User user3 = new User("User Three", "user3@example.com", 35);
        
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class)))
            .thenReturn(user1)
            .thenReturn(user2)
            .thenReturn(user3);

        // Act
        User created1 = userService.createUser(user1);
        User created2 = userService.createUser(user2);
        User created3 = userService.createUser(user3);

        // Assert
        assertNotNull(created1);
        assertNotNull(created2);
        assertNotNull(created3);
        verify(mongoTemplate, times(3)).save(any(User.class));
    }

//    @Test
//    @DisplayName("Should return empty list when no users exist")
//    void testGetAllUsersEmptyList() {
//        // Arrange
//        when(mongoTemplate.findAll(User.class)).thenReturn(List.of());
//
//        // Act
//        List<User> users = userService.getAllUsers();
//
//        // Assert
//        assertNotNull(users);
//        assertTrue(users.isEmpty());
//        assertEquals(0, users.size());
//        verify(mongoTemplate, times(1)).findAll(User.class);
//    }

    @Test
    @DisplayName("Should return single user in list")
    void testGetAllUsersSingleUser() {
        // Arrange
        List<User> singleUserList = List.of(testUser);
        when(mongoTemplate.findAll(User.class)).thenReturn(singleUserList);

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(testUser.getId(), users.get(0).getId());
        verify(mongoTemplate, times(1)).findAll(User.class);
    }

    @Test
    @DisplayName("Should return large list of users")
    void testGetAllUsersLargeList() {
        // Arrange
        List<User> largeUserList = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User("User " + i, "user" + i + "@example.com", 25 + (i % 50));
            user.setId("id" + i);
            largeUserList.add(user);
        }
        when(mongoTemplate.findAll(User.class)).thenReturn(largeUserList);

        // Act
        List<User> users = userService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1000, users.size());
        verify(mongoTemplate, times(1)).findAll(User.class);
    }

    @Test
    @DisplayName("Should return empty Optional for null ID in getUserById")
    void testGetUserByNullId() {
        // Arrange
        when(mongoTemplate.findById(null, User.class)).thenReturn(null);

        // Act
        java.util.Optional<User> result = userService.getUserById(null);

        // Assert
        assertTrue(result.isEmpty());
        verify(mongoTemplate, times(1)).findById(null, User.class);
    }

    @Test
    @DisplayName("Should return empty Optional for empty string ID")
    void testGetUserByEmptyId() {
        // Arrange
        when(mongoTemplate.findById("", User.class)).thenReturn(null);

        // Act
        java.util.Optional<User> result = userService.getUserById("");

        // Assert
        assertTrue(result.isEmpty());
        verify(mongoTemplate, times(1)).findById("", User.class);
    }

    @Test
    @DisplayName("Should return empty Optional for invalid MongoDB ID format")
    void testGetUserByInvalidIdFormat() {
        // Arrange
        String invalidId = "invalid-id-format";
        when(mongoTemplate.findById(invalidId, User.class)).thenReturn(null);

        // Act
        java.util.Optional<User> result = userService.getUserById(invalidId);

        // Assert
        assertTrue(result.isEmpty());
        verify(mongoTemplate, times(1)).findById(invalidId, User.class);
    }

    @Test
    @DisplayName("Should update user with boundary age values")
    void testUpdateUserWithBoundaryAges() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User existingUser = new User("John Doe", "john@example.com", 30);
        existingUser.setId(userId);
        
        User updateDataMin = new User("John Doe", "john@example.com", 18);
        User updateDataMax = new User("John Doe", "john@example.com", 100);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        when(mongoTemplate.save(any(User.class))).thenReturn(existingUser);

        // Act - Update with minimum age
        java.util.Optional<User> updatedMin = userService.updateUser(userId, updateDataMin);
        
        // Act - Update with maximum age
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        java.util.Optional<User> updatedMax = userService.updateUser(userId, updateDataMax);

        // Assert
        assertTrue(updatedMin.isPresent());
        assertTrue(updatedMax.isPresent());
        verify(mongoTemplate, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return false when deleting with null ID")
    void testDeleteUserWithNullId() {
        // Arrange
        when(mongoTemplate.findById(null, User.class)).thenReturn(null);

        // Act
        boolean result = userService.deleteUser(null);

        // Assert
        assertFalse(result);
        verify(mongoTemplate, never()).remove(any(Query.class), eq(User.class));
    }

    @Test
    @DisplayName("Should return false when deleting with empty ID")
    void testDeleteUserWithEmptyId() {
        // Arrange
        when(mongoTemplate.findById("", User.class)).thenReturn(null);

        // Act
        boolean result = userService.deleteUser("");

        // Assert
        assertFalse(result);
        verify(mongoTemplate, never()).remove(any(Query.class), eq(User.class));
    }

    @Test
    @DisplayName("Should handle email case sensitivity")
    void testEmailCaseSensitivity() {
        // Arrange
        User userLowerCase = new User("User One", "test@example.com", 30);
        User userUpperCase = new User("User Two", "TEST@EXAMPLE.COM", 30);
        
        when(mongoTemplate.exists(any(Query.class), eq(User.class)))
            .thenReturn(false)
            .thenReturn(false);
        when(mongoTemplate.save(any(User.class)))
            .thenReturn(userLowerCase)
            .thenReturn(userUpperCase);

        // Act
        User created1 = userService.createUser(userLowerCase);
        User created2 = userService.createUser(userUpperCase);

        // Assert - Service doesn't enforce case sensitivity, that's MongoDB's job
        assertNotNull(created1);
        assertNotNull(created2);
        verify(mongoTemplate, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle concurrent update attempts on same user")
    void testUpdateUserConcurrentScenario() {
        // Arrange
        String userId = "507f1f77bcf86cd799439011";
        User existingUser = new User("John Doe", "john@example.com", 30);
        existingUser.setId(userId);
        
        User update1 = new User("John Update 1", "john1@example.com", 31);
        User update2 = new User("John Update 2", "john2@example.com", 32);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(existingUser);

        // Act - Simulate two updates
        java.util.Optional<User> result1 = userService.updateUser(userId, update1);
        
        when(mongoTemplate.findById(userId, User.class)).thenReturn(existingUser);
        java.util.Optional<User> result2 = userService.updateUser(userId, update2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        verify(mongoTemplate, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle whitespace in user name")
    void testCreateUserWithWhitespaceName() {
        // Arrange
        User userWithSpaces = new User("  John   Doe  ", "john@example.com", 30);
        when(mongoTemplate.exists(any(Query.class), eq(User.class))).thenReturn(false);
        when(mongoTemplate.save(any(User.class))).thenReturn(userWithSpaces);

        // Act
        User created = userService.createUser(userWithSpaces);

        // Assert
        assertNotNull(created);
        assertEquals("  John   Doe  ", created.getName());
        verify(mongoTemplate, times(1)).save(any(User.class));
    }

}