package com.amalitech.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User entity representing a user in the system.
 */
@Document(collection = "users")
@Schema(description = "User entity representing a user in the system")
public final class User {

    /** Maximum length for name field. */
    public static final int MAX_NAME_LENGTH = 255;

    /** Minimum age requirement. */
    public static final int MIN_AGE = 18;

    /** Maximum age allowed. */
    public static final int MAX_AGE = 100;

    @Id
    @Schema(description = "Unique identifier for the user",
            example = "507f1f77bcf86cd799439011",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Name is required and cannot be blank")
    @Schema(description = "Full name of the user",
            example = "John Doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = MAX_NAME_LENGTH)
    private String name;

    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Indexed(unique = true)
    @Schema(description = "Email address of the user (must be unique)",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = MIN_AGE, message = "Age must be at least 18")
    @Max(value = MAX_AGE, message = "Age must not exceed 100")
    @Schema(description = "Age of the user",
            example = "30",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "18",
            maximum = "100")
    private Integer age;

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Constructor with parameters.
     *
     * @param name the user's name
     * @param email the user's email
     * @param age the user's age
     */
    public User(final String name, final String email, final Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id the user ID to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user name.
     *
     * @param name the user name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the user email.
     *
     * @return the user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user email.
     *
     * @param email the user email to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets the user age.
     *
     * @return the user age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the user age.
     *
     * @param age the user age to set
     */
    public void setAge(final Integer age) {
        this.age = age;
    }
}
