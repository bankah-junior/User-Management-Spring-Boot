package com.amalitech.service;

import com.amalitech.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private final MongoTemplate mongoTemplate;
    
    public UserServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @Override
    public User createUser(User user) {
        return mongoTemplate.save(user);
    }
    
    @Override
    public List<User> getAllUsers() {
        return mongoTemplate.findAll(User.class);
    }
    
    @Override
    public Optional<User> getUserById(String id) {
        User user = mongoTemplate.findById(id, User.class);
        return Optional.ofNullable(user);
    }
    
    @Override
    public Optional<User> updateUser(String id, User user) {
        // Check if user exists
        User existingUser = mongoTemplate.findById(id, User.class);
        if (existingUser == null) {
            return Optional.empty();
        }
        
        // Update user fields
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        
        // Save updated user
        User updatedUser = mongoTemplate.save(existingUser);
        return Optional.of(updatedUser);
    }
    
    @Override
    public boolean deleteUser(String id) {
        // Check if user exists
        User existingUser = mongoTemplate.findById(id, User.class);
        if (existingUser == null) {
            return false;
        }
        
        // Delete user
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, User.class);
        return true;
    }
}
