package com.klass.server.course;

import com.mongodb.lang.Nullable;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
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

    @Autowired
    MongoTemplate mongoTemplate;

    //=== Lookup aggregations for GET methods ===//

    // Embed lessons in course
    UnwindOperation unwindLessons = Aggregation.unwind("lessons", true);

    LookupOperation lookupActivities = LookupOperation.newLookup()
            .from("activities")
            .localField("lessons.activities")
            .foreignField("_id")
            .as("lessons.activities");

    GroupOperation groupLessons = Aggregation.group("_id")
            .first("name").as("name")
            .first("slug").as("slug")
            .first("description").as("description")
            .first("image").as("image")
            .first("category").as("category")
            .first("published").as("published")
            .first("instructor").as("instructor")
            .first("students").as("students")
            .push("lessons").as("lessons");


    // Embed instructor in course
    LookupOperation lookupInstructor = LookupOperation.newLookup()
            .from("users")
            .localField("instructor")
            .foreignField("_id")
            .as("instructor");

    UnwindOperation unwindInstructor = Aggregation.unwind("instructor", true);

    // Embed students in course
    LookupOperation lookupStudents = LookupOperation.newLookup()
            .from("users")
            .localField("students")
            .foreignField("_id")
            .as("students");


    //=== REST methods ===//

    // TODO: Add pagination
    // TODO: 1. Role-based projections (GET)
    // TODO: 2. Role-based access control, then abstract postman collections

    // Get all courses
    @GetMapping
    public List<CourseProjection> getAllCourses() {

        Aggregation aggregation = Aggregation.newAggregation(
                // Lessons
                unwindLessons,
                lookupActivities,
                groupLessons,
                // Instructor
                lookupInstructor,
                unwindInstructor,
                // Students
                lookupStudents
        );


        return mongoTemplate.aggregate(
                aggregation,
                "courses",
                CourseProjection.class
        ).getMappedResults();
    }

    // Get course by id
    @GetMapping("/{courseId}")
    @Nullable
    public Optional<CourseProjection> getCourseById(@PathVariable String courseId) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(courseId)),
                // Lessons
                unwindLessons,
                lookupActivities,
                groupLessons,
                // Instructor
                lookupInstructor,
                unwindInstructor,
                // Students
                lookupStudents
        );

        return mongoTemplate.aggregate(
                aggregation,
                "courses",
                CourseProjection.class
        ).getMappedResults().stream().findFirst();
    }

    // Get courses by instructor
    @GetMapping("/instructor/{instructor}")
    public List<Course> getCoursesByInstructor(@PathVariable ObjectId instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    // Get courses by student
    @GetMapping("/student/{student}")
    public List<Course> getCoursesByStudent(@PathVariable ObjectId student) {
        return courseRepository.findByStudent(student);
    }

    // Create course
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        System.out.println(course.toString());
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


}
