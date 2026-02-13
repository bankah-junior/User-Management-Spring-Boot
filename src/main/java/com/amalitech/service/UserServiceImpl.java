package com.amalitech.service;

import com.amalitech.model.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
