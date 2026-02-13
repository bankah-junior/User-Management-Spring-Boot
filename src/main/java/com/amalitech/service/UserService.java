package com.amalitech.service;

import com.amalitech.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    Optional<User> getUserById(String id);
    Optional<User> updateUser(String id, User user);
}
