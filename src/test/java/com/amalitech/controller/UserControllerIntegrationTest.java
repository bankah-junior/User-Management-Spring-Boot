package com.amalitech.controller;

import com.amalitech.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testCreateUser_IntegrationTest() throws Exception {
        // Arrange
        User user = new User("Integration Test User", "integration@test.com", 28);
        String userJson = objectMapper.writeValueAsString(user);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Integration Test User")))
                .andExpect(jsonPath("$.email", is("integration@test.com")))
                .andExpect(jsonPath("$.age", is(28)));
    }
    
    @Test
    void testCreateUser_EndToEnd() throws Exception {
        // Arrange
        User user = new User("End To End User", "e2e@test.com", 35);
        String userJson = objectMapper.writeValueAsString(user);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("End To End User"))
                .andExpect(jsonPath("$.email").value("e2e@test.com"))
                .andExpect(jsonPath("$.age").value(35));
    }
    
    @Test
    void testGetAllUsers_IntegrationTest() throws Exception {
        // Arrange - Create a few users first
        User user1 = new User("Get User 1", "getuser1@test.com", 20);
        User user2 = new User("Get User 2", "getuser2@test.com", 25);
        
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isCreated());
        
        // Act & Assert - Get all users
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(2)));
    }
    
    @Test
    void testGetUserById_IntegrationTest() throws Exception {
        // Arrange - Create a user first
        User user = new User("Get By ID User", "getbyid@test.com", 40);
        String userJson = objectMapper.writeValueAsString(user);
        
        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        User createdUser = objectMapper.readValue(response, User.class);
        String userId = createdUser.getId();
        
        // Act & Assert - Get user by ID
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Get By ID User")))
                .andExpect(jsonPath("$.email", is("getbyid@test.com")))
                .andExpect(jsonPath("$.age", is(40)));
    }
    
    @Test
    void testGetUserById_NotFound() throws Exception {
        // Arrange
        String nonExistentId = "000000000000000000000000";
        
        // Act & Assert - Try to get non-existent user
        mockMvc.perform(get("/api/v1/users/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }
    
    // US-006: Input Validation Integration Tests
    
    @Test
    void testCreateUser_ValidationError_BlankName() throws Exception {
        // Arrange
        User user = new User("", "test@example.com", 25);
        String userJson = objectMapper.writeValueAsString(user);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.fieldErrors.name", is("Name is required and cannot be blank")));
    }
    
    @Test
    void testCreateUser_ValidationError_InvalidEmail() throws Exception {
        // Arrange
        User user = new User("Test User", "invalid-email", 25);
        String userJson = objectMapper.writeValueAsString(user);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.fieldErrors.email", is("Email must be a valid email address")));
    }
    
    @Test
    void testCreateUser_ValidationError_AgeTooLow() throws Exception {
        // Arrange
        User user = new User("Test User", "test@example.com", 17);
        String userJson = objectMapper.writeValueAsString(user);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors.age", is("Age must be at least 18")));
    }
    
    @Test
    void testCreateUser_ValidationError_AgeTooHigh() throws Exception {
        // Arrange
        User user = new User("Test User", "test@example.com", 101);
        String userJson = objectMapper.writeValueAsString(user);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors.age", is("Age must not exceed 100")));
    }
    
    @Test
    void testCreateUser_ValidationError_MultipleFields() throws Exception {
        // Arrange - All fields invalid
        User user = new User("", "invalid-email", 17);
        String userJson = objectMapper.writeValueAsString(user);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }
    
    @Test
    void testCreateUser_ValidationSuccess_BoundaryAge() throws Exception {
        // Test minimum boundary (18)
        User user18 = new User("Test User 18", "test18@example.com", 18);
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user18)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.age", is(18)));
        
        // Test maximum boundary (100)
        User user100 = new User("Test User 100", "test100@example.com", 100);
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user100)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.age", is(100)));
    }
    
    // US-004: Update User Integration Tests
    
    @Test
    void testUpdateUser_IntegrationTest() throws Exception {
        // Arrange - Create a user first
        User user = new User("Original User", "original@test.com", 30);
        String userJson = objectMapper.writeValueAsString(user);
        
        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        User createdUser = objectMapper.readValue(response, User.class);
        String userId = createdUser.getId();
        
        // Prepare update data
        User updateData = new User("Updated User", "updated@test.com", 35);
        String updateJson = objectMapper.writeValueAsString(updateData);
        
        // Act & Assert - Update the user
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated@test.com")))
                .andExpect(jsonPath("$.age", is(35)));
    }
    
    @Test
    void testUpdateUser_NotFound() throws Exception {
        // Arrange
        String nonExistentId = "000000000000000000000000";
        User updateData = new User("Updated User", "updated@test.com", 35);
        String updateJson = objectMapper.writeValueAsString(updateData);
        
        // Act & Assert - Try to update non-existent user
        mockMvc.perform(put("/api/v1/users/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }
    
    @Test
    void testUpdateUser_ValidationError() throws Exception {
        // Arrange - Create a user first
        User user = new User("Valid User", "valid@test.com", 25);
        String userJson = objectMapper.writeValueAsString(user);
        
        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        User createdUser = objectMapper.readValue(response, User.class);
        String userId = createdUser.getId();
        
        // Prepare invalid update data
        User invalidUpdateData = new User("", "invalid-email", 17);
        String invalidJson = objectMapper.writeValueAsString(invalidUpdateData);
        
        // Act & Assert - Try to update with invalid data
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }
    
    @Test
    void testUpdateUser_PartialUpdate() throws Exception {
        // Arrange - Create a user first
        User user = new User("Partial User", "partial@test.com", 40);
        String userJson = objectMapper.writeValueAsString(user);
        
        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        User createdUser = objectMapper.readValue(response, User.class);
        String userId = createdUser.getId();
        
        // Update only name and email
        User updateData = new User("Updated Name Only", "updated.email@test.com", 40);
        String updateJson = objectMapper.writeValueAsString(updateData);
        
        // Act & Assert - Update the user
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.name", is("Updated Name Only")))
                .andExpect(jsonPath("$.email", is("updated.email@test.com")))
                .andExpect(jsonPath("$.age", is(40)));
    }
    
    // US-005: Delete User Integration Tests
    
    @Test
    void testDeleteUser_IntegrationTest() throws Exception {
        // Arrange - Create a user first
        User user = new User("Delete Test User", "delete@test.com", 30);
        String userJson = objectMapper.writeValueAsString(user);
        
        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        User createdUser = objectMapper.readValue(response, User.class);
        String userId = createdUser.getId();
        
        // Act & Assert - Delete the user
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());
        
        // Verify user is deleted by trying to get it
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Arrange
        String nonExistentId = "000000000000000000000000";
        
        // Act & Assert - Try to delete non-existent user
        mockMvc.perform(delete("/api/v1/users/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("User not found")));
    }
    
    @Test
    void testDeleteUser_VerifyRemoval() throws Exception {
        // Arrange - Create a user
        User user = new User("Removal Test", "removal@test.com", 25);
        String userJson = objectMapper.writeValueAsString(user);
        
        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        User createdUser = objectMapper.readValue(response, User.class);
        String userId = createdUser.getId();
        
        // Verify user exists
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk());
        
        // Act - Delete the user
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());
        
        // Assert - Verify user no longer exists
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteUser_CompleteLifecycle() throws Exception {
        // Create, Read, Update, Delete lifecycle test
        
        // 1. Create
        User user = new User("Lifecycle User", "lifecycle@test.com", 30);
        String createResponse = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        User createdUser = objectMapper.readValue(createResponse, User.class);
        String userId = createdUser.getId();
        
        // 2. Read
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Lifecycle User")));
        
        // 3. Update
        User updateData = new User("Updated Lifecycle", "updated.lifecycle@test.com", 35);
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Lifecycle")));
        
        // 4. Delete
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());
        
        // 5. Verify deletion
        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
