package com.klass.server.controller;

import com.klass.server.model.Course;
import com.klass.server.model.Lesson;
import com.klass.server.repository.CourseRepository;
import com.mongodb.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;


    @Autowired
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    // Get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get courses by instructor
    @GetMapping("/instructor/{instructor}")
    public List<Course> getCoursesByInstructor(@PathVariable String instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    // Get courses by student
    @GetMapping("/student/{student}")
    public List<Course> getCoursesByStudent(@PathVariable String student) {
        return courseRepository.findByStudent(student);
    }

    // Get course by id
    @GetMapping("/{courseId}")
    @Nullable
    public Optional<Course> getCourseById(@PathVariable String courseId) {
        return courseRepository.findById(courseId);
    }

    // Create course
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    // Update course
    @PutMapping("/{courseId}")
    public Course updateCourse(@PathVariable String courseId, @RequestBody Course course) {
        course.setId(courseId);
        return courseRepository.save(course);
    }

    // Delete course
    @DeleteMapping("/{courseId}")
    public void deleteCourse(@PathVariable String courseId) {
        courseRepository.deleteById(courseId);
    }

    // Additional methods

    // Publish/unpublish course
    @PutMapping("/{courseId}/publish")
    public void publishCourse(@PathVariable String courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            courseToUpdate.setPublished(!courseToUpdate.isPublished());
            courseRepository.save(courseToUpdate);
        }
    }

    // Lessons

    // Add lesson to course
    @PutMapping("/{courseId}/lessons")
    public void addLessonToCourse(@PathVariable String courseId, @RequestBody Lesson lesson) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<Lesson> lessons = courseToUpdate.getLessons();
            lessons.add(lesson);
            courseToUpdate.setLessons(lessons);
            courseRepository.save(courseToUpdate);
        }
    }

    // Update lesson in course
    @PutMapping("/{courseId}/lessons/{lessonOrder}")
    public void updateLessonInCourse(@PathVariable String courseId, @PathVariable int lessonOrder, @RequestBody Lesson lesson) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<Lesson> lessons = courseToUpdate.getLessons();
            lessons.set(lessonOrder-1, lesson);
            courseToUpdate.setLessons(lessons);
            courseRepository.save(courseToUpdate);
        }
    }

    // Remove lesson from course
    @DeleteMapping("/{courseId}/lessons/{lessonOrder}")
    public void removeLessonFromCourse(@PathVariable String courseId, @PathVariable int lessonOrder) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<Lesson> lessons = courseToUpdate.getLessons();
            lessons.remove(lessonOrder-1);
            courseToUpdate.setLessons(lessons);
            courseRepository.save(courseToUpdate);
        }
    }

}
