package com.klass.server.controller;

import com.klass.server.model.User;
import com.klass.server.repository.UserRepository;
import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) { this.userRepository = userRepository; }

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get all users by role
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{role}")
    public List<User> getAllUsersByRole(@PathVariable String role) {
        return userRepository.findAllByRole(role);
    }

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
        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Update user with UpdateFirst
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
