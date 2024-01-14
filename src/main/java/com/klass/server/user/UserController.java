package com.klass.server.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users
    // TODO filtering and pagination
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserProjection>> getAllUsers() {
        List<UserProjection> users = userRepository.findAll().stream().map(User::toProjection).toList();
        return ResponseEntity.ok(users);
    }

    // Get all users by role
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getAllUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userRepository.findAllByRole(role));
    }

    // Get user by id
    @GetMapping("/{userId}")
    public ResponseEntity<UserProjection> getUserById(@PathVariable String userId) {
        try {
            User user = userRepository.findById(userId).get();
            return ResponseEntity.ok(user.toProjection());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Get user by email
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserProjection> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userRepository.findByEmail(email).toProjection());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    // Create user
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user, UriComponentsBuilder uriComponentsBuilder) {
        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);
        URI url = uriComponentsBuilder.path("/users/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(url).body(newUser);
    }

    // Update user with UpdateFirst (Check if working) (TODO: Update all except password)
    @PutMapping("/{userId}")
    public ResponseEntity<UserProjection> updateUser(@PathVariable String userId, @RequestBody User user) {
        user.setId(userId);
        return ResponseEntity.ok(userRepository.save(user).toProjection());
    }

    @PreAuthorize("hasRole('ADMIN')")
    // Delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    // TODO change password


}
