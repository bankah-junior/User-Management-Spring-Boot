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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing user resources.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management",
     description = "APIs for managing user resources")
public final class UserController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService the user service
     */
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user with HTTP 201 status
     */
    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided "
                    + "information. Email must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"id\":\"507f1f77bcf86cd799439011\","
                          + "\"name\":\"John Doe\","
                          + "\"email\":\"john.doe@example.com\","
                          + "\"age\":30}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - validation failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\","
                          + "\"status\":400,"
                          + "\"error\":\"Bad Request\","
                          + "\"message\":\"Validation failed\","
                          + "\"fieldErrors\":{\"email\":"
                          + "\"Email must be a valid email address\"}}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\","
                          + "\"status\":409,"
                          + "\"error\":\"Conflict\","
                          + "\"message\":\"Email already exists: "
                          + "john.doe@example.com\"}"
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
                    value = "{\"name\":\"John Doe\","
                          + "\"email\":\"john.doe@example.com\","
                          + "\"age\":30}"
                )
            )
        )
        @Valid @RequestBody final User user) {
        LOGGER.info("Received POST request to create user with email: {}",
                    user.getEmail());
        User createdUser = userService.createUser(user);
        LOGGER.info("Successfully created user with ID: {}",
                    createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Retrieves all users.
     *
     * @return list of all users
     */
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
                    value = "[{\"id\":\"507f1f77bcf86cd799439011\","
                          + "\"name\":\"John Doe\","
                          + "\"email\":\"john.doe@example.com\","
                          + "\"age\":30},"
                          + "{\"id\":\"507f1f77bcf86cd799439012\","
                          + "\"name\":\"Jane Smith\","
                          + "\"email\":\"jane.smith@example.com\","
                          + "\"age\":25}]"
                )
            )
        )
    })
    public ResponseEntity<List<User>> getAllUsers() {
        LOGGER.info("Received GET request to fetch all users");
        List<User> users = userService.getAllUsers();
        LOGGER.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user if found
     */
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
                    value = "{\"id\":\"507f1f77bcf86cd799439011\","
                          + "\"name\":\"John Doe\","
                          + "\"email\":\"john.doe@example.com\","
                          + "\"age\":30}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\","
                          + "\"status\":404,"
                          + "\"error\":\"Not Found\","
                          + "\"message\":\"User not found with id: "
                          + "507f1f77bcf86cd799439011\"}"
                )
            )
        )
    })
    public ResponseEntity<User> getUserById(
        @Parameter(description = "Unique identifier of the user",
                   example = "507f1f77bcf86cd799439011")
        @PathVariable final String id) {
        LOGGER.info("Received GET request for user ID: {}", id);
        return userService.getUserById(id)
                .map(user -> {
                    LOGGER.info("Successfully found user ID: {}", id);
                    return ResponseEntity.ok(user);
                })
                .orElseThrow(() -> {
                    LOGGER.warn("User not found with ID: {}", id);
                    return new UserNotFoundException(id);
                });
    }

    /**
     * Updates an existing user.
     *
     * @param id the user ID
     * @param user the updated user data
     * @return the updated user
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing user",
        description = "Updates all fields of an existing user. "
                    + "Email must remain unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"id\":\"507f1f77bcf86cd799439011\","
                          + "\"name\":\"John Updated\","
                          + "\"email\":\"john.updated@example.com\","
                          + "\"age\":35}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - validation failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\","
                          + "\"status\":400,"
                          + "\"error\":\"Bad Request\","
                          + "\"message\":\"Validation failed\","
                          + "\"fieldErrors\":{\"age\":"
                          + "\"Age must be at least 18\"}}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\","
                          + "\"status\":404,"
                          + "\"error\":\"Not Found\","
                          + "\"message\":\"User not found with id: "
                          + "507f1f77bcf86cd799439011\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\","
                          + "\"status\":409,"
                          + "\"error\":\"Conflict\","
                          + "\"message\":\"Email already exists: "
                          + "existing@example.com\"}"
                )
            )
        )
    })
    public ResponseEntity<User> updateUser(
        @Parameter(description = "Unique identifier of the user to update",
                   example = "507f1f77bcf86cd799439011")
        @PathVariable final String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated user object",
            required = true,
            content = @Content(
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    value = "{\"name\":\"John Updated\","
                          + "\"email\":\"john.updated@example.com\","
                          + "\"age\":35}"
                )
            )
        )
        @Valid @RequestBody final User user) {
        LOGGER.info("Received PUT request to update user ID: {}", id);
        return userService.updateUser(id, user)
                .map(updatedUser -> {
                    LOGGER.info("Successfully updated user ID: {}", id);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseThrow(() -> {
                    LOGGER.warn("User not found for update with ID: {}", id);
                    return new UserNotFoundException(id);
                });
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID
     * @return empty response with HTTP 204 status
     */
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
                    value = "{\"timestamp\":\"2026-02-13T02:22:32.034Z\","
                          + "\"status\":404,"
                          + "\"error\":\"Not Found\","
                          + "\"message\":\"User not found with id: "
                          + "507f1f77bcf86cd799439011\"}"
                )
            )
        )
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "Unique identifier of the user to delete",
                   example = "507f1f77bcf86cd799439011")
        @PathVariable final String id) {
        LOGGER.info("Received DELETE request for user ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            LOGGER.info("Successfully deleted user ID: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            LOGGER.warn("User not found for deletion with ID: {}", id);
            throw new UserNotFoundException(id);
        }
    }
}
