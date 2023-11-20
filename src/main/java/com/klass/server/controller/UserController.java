package com.klass.server.controller;

import com.klass.server.model.User;
import com.klass.server.repository.UserRepository;
import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) { this.userRepository = userRepository; }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    @Nullable
    public Optional<User> getUserById(@PathVariable String userId) {
        return userRepository.findById(userId);

    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        user.setId(userId);
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
    }

    // Additonal methods

    // Add course to user
    @PutMapping("/{userId}/addCourse/{courseId}")
    public void addCourseToUser(@PathVariable String userId, @PathVariable String courseId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User userToUpdate = user.get();
            List<String> courses = userToUpdate.getCourses();
            courses.add(courseId);
            userToUpdate.setCourses(courses);
            userRepository.save(userToUpdate);
        }
    }


}
