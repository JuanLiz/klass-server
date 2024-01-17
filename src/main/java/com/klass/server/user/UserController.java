package com.klass.server.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

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
    // TODO filtering
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserProjection>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userRepository.findAll(pageable).map(User::toProjection));
    }

    // Get user by id
    @GetMapping("/{userId}")
    public ResponseEntity<UserProjection> getUserById(@PathVariable String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> ResponseEntity.ok(value.toProjection()))
                .orElseGet(() -> ResponseEntity.notFound().build());

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
    public ResponseEntity<UserProjection> createUser(@RequestBody @Valid User user, UriComponentsBuilder uriComponentsBuilder) {
        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User newUser = userRepository.save(user);
        URI url = uriComponentsBuilder.path("/users/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(url).body(newUser.toProjection());
    }

    // Update user
    @PutMapping("/{userId}")
    public ResponseEntity<UserProjection> updateUser(@PathVariable String userId, @RequestBody UserProjection user) {

        // Check if user exists
        Optional<User> userToUpdate = userRepository.findById(userId);
        if (userToUpdate.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            User userToSave = userToUpdate.get();
            userToSave.setRole(user.role());
            userToSave.setName(user.name());
            userToSave.setLastName(user.lastName());
            userToSave.setEmail(user.email());
            userToSave.setPicture(user.picture());
            userRepository.save(userToSave);
            return ResponseEntity.ok(userToSave.toProjection());
        }
    }

    // Delete user
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    // TODO change password (SES)


}
