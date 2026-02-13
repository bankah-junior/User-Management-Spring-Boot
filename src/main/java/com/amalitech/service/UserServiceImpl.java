package com.amalitech.service;

import com.amalitech.exception.DuplicateEmailException;
import com.amalitech.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final MongoTemplate mongoTemplate;
    
    public UserServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public User createUser(User user) {
        logger.debug("Creating new user with email: {}", user.getEmail());
        
        // Check if email already exists
        if (emailExists(user.getEmail())) {
            logger.warn("Attempt to create user with duplicate email: {}", user.getEmail());
            throw new DuplicateEmailException(user.getEmail());
        }
        
        try {
            User savedUser = mongoTemplate.save(user);
            logger.info("Successfully created user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user with email: {}", user.getEmail(), e);
            throw e;
        }
    }
    
    @Override
    public List<User> getAllUsers() {
        logger.debug("Fetching all users from database");
        
        try {
            List<User> users = mongoTemplate.findAll(User.class);
            logger.info("Successfully retrieved {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw e;
        }
    }
    
    @Override
    public Optional<User> getUserById(String id) {
        logger.debug("Fetching user by ID: {}", id);
        
        try {
            User user = mongoTemplate.findById(id, User.class);
            if (user != null) {
                logger.info("Successfully found user with ID: {}", id);
            } else {
                logger.warn("User not found with ID: {}", id);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error fetching user by ID: {}", id, e);
            throw e;
        }
    }
    
    @Override
    public Optional<User> updateUser(String id, User user) {
        logger.debug("Updating user with ID: {}", id);
        
        // Check if user exists
        User existingUser = mongoTemplate.findById(id, User.class);
        if (existingUser == null) {
            logger.warn("Attempt to update non-existent user with ID: {}", id);
            return Optional.empty();
        }
        
        // Check if email is being changed and new email already exists
        if (!existingUser.getEmail().equals(user.getEmail()) && emailExists(user.getEmail())) {
            logger.warn("Attempt to update user {} with duplicate email: {}", id, user.getEmail());
            throw new DuplicateEmailException(user.getEmail());
        }
        
        try {
            // Update user fields
            String oldEmail = existingUser.getEmail();
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setAge(user.getAge());
            
            // Save updated user
            User updatedUser = mongoTemplate.save(existingUser);
            logger.info("Successfully updated user with ID: {}. Email changed from {} to {}", 
                id, oldEmail, updatedUser.getEmail());
            return Optional.of(updatedUser);
        } catch (DuplicateEmailException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", id, e);
            throw e;
        }
    }
    
    @Override
    public boolean deleteUser(String id) {
        logger.debug("Deleting user with ID: {}", id);
        
        // Check if user exists
        User existingUser = mongoTemplate.findById(id, User.class);
        if (existingUser == null) {
            logger.warn("Attempt to delete non-existent user with ID: {}", id);
            return false;
        }
        
        try {
            // Delete user
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, User.class);
            logger.info("Successfully deleted user with ID: {} (email: {})", id, existingUser.getEmail());
            return true;
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            throw e;
        }
    }
    
    private boolean emailExists(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.exists(query, User.class);
    }
}
