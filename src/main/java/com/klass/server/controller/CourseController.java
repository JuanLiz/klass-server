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

    // TODO Get courses by instructor
    @GetMapping("/instructor/{instructor}")
    public List<Course> getCoursesByInstructor(@PathVariable String instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    // TODO Get courses by student
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

    // TODO Publish/unpublish course
    @PutMapping("/{courseId}/publish")
    public void publishCourse(@PathVariable String courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            courseToUpdate.setPublished(!courseToUpdate.isPublished());
            courseRepository.save(courseToUpdate);
        }
    }


    // TODO Students

    // Add student to course
    @PutMapping("/{courseId}/students/{studentId}")
    public void addStudentToCourse(@PathVariable String courseId, @PathVariable String studentId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<String> students = courseToUpdate.getStudents();
            students.add(studentId);
            courseToUpdate.setStudents(students);
            courseRepository.save(courseToUpdate);
        }
    }

    // Remove student from course
    @DeleteMapping("/{courseId}/students/{studentId}")
    public void removeStudentFromCourse(@PathVariable String courseId, @PathVariable String studentId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<String> students = courseToUpdate.getStudents();
            students.remove(studentId);
            courseToUpdate.setStudents(students);
            courseRepository.save(courseToUpdate);
        }
    }

    // TODO Lessons

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

    // Remove lesson from course
    @DeleteMapping("/{courseId}/lessons")
    public void removeLessonFromCourse(@PathVariable String courseId, @RequestBody Lesson lesson) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<Lesson> lessons = courseToUpdate.getLessons();
            lessons.remove(lesson);
            courseToUpdate.setLessons(lessons);
            courseRepository.save(courseToUpdate);
        }
    }

    // Add activity to lesson
    @PutMapping("/{courseId}/lessons/{lessonOrder}/activities/{activity}")
    public void addActivityToLesson(@PathVariable String courseId, @PathVariable int lessonOrder, @PathVariable String activity) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<Lesson> lessons = courseToUpdate.getLessons();
            Lesson lessonToUpdate = lessons.get(lessonOrder);
            List<String> activities = lessonToUpdate.getActivities();
            activities.add(activity);
            lessonToUpdate.setActivities(activities);
            lessons.set(lessonOrder, lessonToUpdate);
            courseToUpdate.setLessons(lessons);
            courseRepository.save(courseToUpdate);
        }
    }

    // Remove activity from lesson
    @DeleteMapping("/{courseId}/lessons/{lessonOrder}/activities/{activity}")
    public void removeActivityFromLesson(@PathVariable String courseId, @PathVariable int lessonOrder, @PathVariable String activity) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            List<Lesson> lessons = courseToUpdate.getLessons();
            Lesson lessonToUpdate = lessons.get(lessonOrder);
            List<String> activities = lessonToUpdate.getActivities();
            activities.remove(activity);
            lessonToUpdate.setActivities(activities);
            lessons.set(lessonOrder, lessonToUpdate);
            courseToUpdate.setLessons(lessons);
            courseRepository.save(courseToUpdate);
        }
    }

}
