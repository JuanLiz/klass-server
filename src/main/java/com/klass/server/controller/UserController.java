package com.klass.server.controller;

import com.klass.server.model.User;
import com.klass.server.repository.UserRepository;
import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) { this.userRepository = userRepository; }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //TODO: Get all users by role

    // Get user by id
    @GetMapping("/{userId}")
    @Nullable
    public Optional<User> getUserById(@PathVariable String userId) {
        return userRepository.findById(userId);
    }

    // Get user by email
    @GetMapping("/email/{email}")
    @Nullable
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email);
    }

    // Create user
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // Update user
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        user.setId(userId);
        return userRepository.save(user);
    }

    // Delete user
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
    }


}
