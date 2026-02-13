package com.amalitech.service;

import com.amalitech.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
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
}
