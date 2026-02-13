package com.amalitech.validation;

import com.amalitech.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Validation Tests")
class UserValidationTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Should pass validation with valid user data")
    void testValidUser() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertTrue(violations.isEmpty(), "Valid user should have no violations");
    }
    
    @Test
    @DisplayName("Should fail validation when name is blank")
    void testBlankName() {
        // Arrange
        User user = new User("", "john@example.com", 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Blank name should cause validation error");
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Name is required and cannot be blank", violation.getMessage());
    }
    
    @Test
    @DisplayName("Should fail validation when name is null")
    void testNullName() {
        // Arrange
        User user = new User(null, "john@example.com", 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Null name should cause validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }
    
    @Test
    @DisplayName("Should fail validation when email is blank")
    void testBlankEmail() {
        // Arrange
        User user = new User("John Doe", "", 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Blank email should cause validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
    
    @Test
    @DisplayName("Should fail validation when email format is invalid")
    void testInvalidEmailFormat() {
        // Arrange
        User user = new User("John Doe", "invalid-email", 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Invalid email format should cause validation error");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("Email must be a valid email address", violation.getMessage());
    }
    
    @Test
    @DisplayName("Should fail validation when email is null")
    void testNullEmail() {
        // Arrange
        User user = new User("John Doe", null, 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Null email should cause validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
    
    @Test
    @DisplayName("Should fail validation when age is null")
    void testNullAge() {
        // Arrange
        User user = new User("John Doe", "john@example.com", null);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Null age should cause validation error");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("age", violation.getPropertyPath().toString());
        assertEquals("Age is required", violation.getMessage());
    }
    
    @Test
    @DisplayName("Should fail validation when age is less than 18")
    void testAgeTooLow() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 17);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Age below 18 should cause validation error");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("age", violation.getPropertyPath().toString());
        assertEquals("Age must be at least 18", violation.getMessage());
    }
    
    @Test
    @DisplayName("Should fail validation when age is greater than 100")
    void testAgeTooHigh() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 101);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty(), "Age above 100 should cause validation error");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("age", violation.getPropertyPath().toString());
        assertEquals("Age must not exceed 100", violation.getMessage());
    }
    
    @Test
    @DisplayName("Should pass validation with boundary age values")
    void testBoundaryAgeValues() {
        // Test minimum boundary (18)
        User user18 = new User("John Doe", "john@example.com", 18);
        Set<ConstraintViolation<User>> violations18 = validator.validate(user18);
        assertTrue(violations18.isEmpty(), "Age 18 should be valid");
        
        // Test maximum boundary (100)
        User user100 = new User("Jane Doe", "jane@example.com", 100);
        Set<ConstraintViolation<User>> violations100 = validator.validate(user100);
        assertTrue(violations100.isEmpty(), "Age 100 should be valid");
    }
    
    @Test
    @DisplayName("Should have multiple violations for multiple invalid fields")
    void testMultipleValidationErrors() {
        // Arrange
        User user = new User("", "invalid-email", 17);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertTrue(violations.size() >= 3, "Should have at least 3 validation errors");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("age")));
    }

    // US-009: Additional Validation Tests

    @Test
    @DisplayName("Should pass validation with complex valid email")
    void testComplexValidEmail() {
        // Arrange
        User user = new User("User", "user+tag@sub-domain.example.com", 30);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertTrue(violations.isEmpty(), "Complex valid email should pass");
    }

    @Test
    @DisplayName("Should fail validation with email missing @")
    void testEmailMissingAt() {
        // Arrange
        User user = new User("John Doe", "johnexample.com", 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") && 
            v.getMessage().contains("valid email")));
    }

    @Test
    @DisplayName("Should fail validation with email missing domain")
    void testEmailMissingDomain() {
        // Arrange
        User user = new User("John Doe", "john@", 25);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation when all fields are invalid")
    void testAllFieldsInvalid() {
        // Arrange
        User user = new User("", "", 0);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertTrue(violations.size() >= 3);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("age")));
    }

    @Test
    @DisplayName("Should pass validation with special characters in name")
    void testSpecialCharactersInName() {
        // Arrange
        User user = new User("José María O'Brien-Smith", "jose@example.com", 30);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertTrue(violations.isEmpty(), "Name with special characters should be valid");
    }

    @Test
    @DisplayName("Should pass validation with long valid name")
    void testLongValidName() {
        // Arrange
        String longName = "A".repeat(100);
        User user = new User(longName, "test@example.com", 30);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertTrue(violations.isEmpty(), "Long name should be valid if not blank");
    }

    @Test
    @DisplayName("Should fail validation with only whitespace name")
    void testWhitespaceOnlyName() {
        // Arrange
        User user = new User("   ", "test@example.com", 30);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("name") && 
            v.getMessage().contains("blank")));
    }

    @Test
    @DisplayName("Should fail validation with only whitespace email")
    void testWhitespaceOnlyEmail() {
        // Arrange
        User user = new User("John Doe", "   ", 30);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation with age exactly below minimum (17)")
    void testAgeBelowMinimumBoundary() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 17);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("age") && 
            v.getMessage().contains("at least 18")));
    }

    @Test
    @DisplayName("Should fail validation with age exactly above maximum (101)")
    void testAgeAboveMaximumBoundary() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 101);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("age") && 
            v.getMessage().contains("not exceed 100")));
    }

    @Test
    @DisplayName("Should fail validation with negative age")
    void testNegativeAge() {
        // Arrange
        User user = new User("John Doe", "john@example.com", -5);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("age")));
    }

    @Test
    @DisplayName("Should fail validation with extremely large age")
    void testExtremelyLargeAge() {
        // Arrange
        User user = new User("John Doe", "john@example.com", 999);
        
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("age") && 
            v.getMessage().contains("not exceed 100")));
    }

    @Test
    @DisplayName("Should validate exact error messages for each field")
    void testExactErrorMessages() {
        // Test name
        User userBlankName = new User("", "john@example.com", 25);
        Set<ConstraintViolation<User>> nameViolations = validator.validate(userBlankName);
        assertTrue(nameViolations.stream().anyMatch(v -> 
            v.getMessage().equals("Name is required and cannot be blank")));
        
        // Test email
        User userInvalidEmail = new User("John", "invalid", 25);
        Set<ConstraintViolation<User>> emailViolations = validator.validate(userInvalidEmail);
        assertTrue(emailViolations.stream().anyMatch(v -> 
            v.getMessage().equals("Email must be a valid email address")));
        
        // Test age min
        User userAgeTooLow = new User("John", "john@example.com", 17);
        Set<ConstraintViolation<User>> ageLowViolations = validator.validate(userAgeTooLow);
        assertTrue(ageLowViolations.stream().anyMatch(v -> 
            v.getMessage().equals("Age must be at least 18")));
        
        // Test age max
        User userAgeTooHigh = new User("John", "john@example.com", 101);
        Set<ConstraintViolation<User>> ageHighViolations = validator.validate(userAgeTooHigh);
        assertTrue(ageHighViolations.stream().anyMatch(v -> 
            v.getMessage().equals("Age must not exceed 100")));
    }
}
