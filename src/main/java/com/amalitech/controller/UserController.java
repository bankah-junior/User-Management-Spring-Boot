package com.amalitech.controller;

import com.amalitech.exception.UserNotFoundException;
import com.amalitech.model.User;
import com.amalitech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing user resources")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided information. Email must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"id\":\"507f1f77bcf86cd799439011\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"age\":30}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - validation failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Validation failed\",\"fieldErrors\":{\"email\":\"Email must be a valid email address\"}}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\",\"status\":409,\"error\":\"Conflict\",\"message\":\"Email already exists: john.doe@example.com\"}"
                )
            )
        )
    })
    public ResponseEntity<User> createUser(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User object to be created",
            required = true,
            content = @Content(
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"age\":30}"
                )
            )
        )
        @Valid @RequestBody User user) {
        logger.info("Received POST request to create user with email: {}", user.getEmail());
        User createdUser = userService.createUser(user);
        logger.info("Successfully created user with ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all users",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "[{\"id\":\"507f1f77bcf86cd799439011\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"age\":30},{\"id\":\"507f1f77bcf86cd799439012\",\"name\":\"Jane Smith\",\"email\":\"jane.smith@example.com\",\"age\":25}]"
                )
            )
        )
    })
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Received GET request to fetch all users");
        List<User> users = userService.getAllUsers();
        logger.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"id\":\"507f1f77bcf86cd799439011\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"age\":30}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found with id: 507f1f77bcf86cd799439011\"}"
                )
            )
        )
    })
    public ResponseEntity<User> getUserById(
        @Parameter(description = "Unique identifier of the user", example = "507f1f77bcf86cd799439011")
        @PathVariable String id) {
        logger.info("Received GET request for user ID: {}", id);
        return userService.getUserById(id)
                .map(user -> {
                    logger.info("Successfully found user ID: {}", id);
                    return ResponseEntity.ok(user);
                })
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException(id);
                });
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing user",
        description = "Updates all fields of an existing user. Email must remain unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"id\":\"507f1f77bcf86cd799439011\",\"name\":\"John Updated\",\"email\":\"john.updated@example.com\",\"age\":35}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - validation failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Validation failed\",\"fieldErrors\":{\"age\":\"Age must be at least 18\"}}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found with id: 507f1f77bcf86cd799439011\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\",\"status\":409,\"error\":\"Conflict\",\"message\":\"Email already exists: existing@example.com\"}"
                )
            )
        )
    })
    public ResponseEntity<User> updateUser(
        @Parameter(description = "Unique identifier of the user to update", example = "507f1f77bcf86cd799439011")
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated user object",
            required = true,
            content = @Content(
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"name\":\"John Updated\",\"email\":\"john.updated@example.com\",\"age\":35}"
                )
            )
        )
        @Valid @RequestBody User user) {
        logger.info("Received PUT request to update user ID: {}", id);
        return userService.updateUser(id, user)
                .map(updatedUser -> {
                    logger.info("Successfully updated user ID: {}", id);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseThrow(() -> {
                    logger.warn("User not found for update with ID: {}", id);
                    return new UserNotFoundException(id);
                });
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a user",
        description = "Permanently deletes a user from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found with id: 507f1f77bcf86cd799439011\"}"
                )
            )
        )
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "Unique identifier of the user to delete", example = "507f1f77bcf86cd799439011")
        @PathVariable String id) {
        logger.info("Received DELETE request for user ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            logger.info("Successfully deleted user ID: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("User not found for deletion with ID: {}", id);
            throw new UserNotFoundException(id);
        }
    }
}
