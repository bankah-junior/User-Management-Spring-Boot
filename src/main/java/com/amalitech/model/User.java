package com.amalitech.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Schema(description = "User entity representing a user in the system")
public class User {
    
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
            maxLength = 255)
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
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must not exceed 100")
    @Schema(description = "Age of the user", 
            example = "30", 
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "18",
            maximum = "100")
    private Integer age;
    
    public User() {
    }
    
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
}
