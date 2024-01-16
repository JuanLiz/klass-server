package com.klass.server.course;

import com.klass.server.user.UserRepository;
import com.mongodb.lang.Nullable;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedList;
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

    @Autowired
    UserRepository userRepository;

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


    // All aggregations in order
    LinkedList<AggregationOperation> courseAggregations = new LinkedList<>(List.of(
            // Lessons
            unwindLessons,
            lookupActivities,
            groupLessons,
            // Instructor
            lookupInstructor,
            unwindInstructor,
            // Students
            lookupStudents
    ));


    //=== REST methods ===//

    // TODO: Add pagination and filtering
    // TODO: 2. Abstract postman collections
    // TODO Slug availability

    // Get all courses
    @GetMapping
    public ResponseEntity<List<CourseProjection>> getAllCourses() {

        // Get current authentication
        var auth = SecurityContextHolder.getContext()
                .getAuthentication();

        // Get user id
        ObjectId userId = new ObjectId(userRepository.findByEmail(auth.getName()).getId());

        LinkedList<AggregationOperation> aggregationList = courseAggregations;

        // Check user role (TODO: Is this boilerplate?)
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))) {
            // Add match to previous aggregation
            aggregationList.addFirst(Aggregation.match(Criteria.where("instructor").is(userId)));
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            aggregationList.addFirst(Aggregation.match(Criteria.where("students").in(userId)));
        }

        return ResponseEntity.ok(mongoTemplate.aggregate(
                Aggregation.newAggregation(aggregationList),
                "courses",
                CourseProjection.class
        ).getMappedResults());
    }

    // Get course by id
    @GetMapping("/{courseId}")
    @Nullable
    public ResponseEntity<CourseProjection> getCourseById(@PathVariable String courseId) {
        try {

            // Get current authentication
            var auth = SecurityContextHolder.getContext()
                    .getAuthentication();

            // Get user id
            ObjectId userId = new ObjectId(userRepository.findByEmail(auth.getName()).getId());

            // Dummy match for initialize
            MatchOperation match = Aggregation.match(Criteria.where("_id").exists(true));

            // Check user role
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))) {
                match = Aggregation.match(Criteria.where("instructor").is(userId));
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
                match = Aggregation.match(Criteria.where("students").in(userId));
            }

            // Use MongoRepository for validations
            Optional<Course> course = courseRepository.findById(courseId);

            // Check if course exists
            if (course.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            // Check if user is enrolled
            else if (
                    // Student
                    (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))
                            && !course.get().getStudents().contains(userId))
                            // Instructor
                            || (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))
                            && !course.get().getInstructor().equals(userId))) {
                return ResponseEntity.notFound().build();
            }
            // Check if course is published
            else if (!course.get().isPublished()
                    && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
                return ResponseEntity.notFound().build();
            }
            // Return course
            else {
                LinkedList<AggregationOperation> aggregationList = courseAggregations;
                // Add match and filter to previous aggregation
                aggregationList.addFirst(match);
                aggregationList.addFirst(Aggregation.match(Criteria.where("_id").is(courseId)));

                return ResponseEntity.ok(mongoTemplate.aggregate(
                        Aggregation.newAggregation(aggregationList),
                        "courses",
                        CourseProjection.class
                ).getUniqueMappedResult());
            }

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get courses by instructor TODO: Deprecation with filter and pagination
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/instructor/{instructor}")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable ObjectId instructor) {
        return ResponseEntity.ok(courseRepository.findByInstructor(instructor));
    }

    // Get courses by student TODO: Deprecation with filter and pagination
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/student/{student}")
    public ResponseEntity<List<Course>> getCoursesByStudent(@PathVariable ObjectId student) {
        return ResponseEntity.ok(courseRepository.findByStudent(student));
    }

    // Create course
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody @Valid Course course, UriComponentsBuilder uriComponentsBuilder) {
        Course newCourse = courseRepository.save(course);
        URI url = uriComponentsBuilder.path("/courses/{id}").buildAndExpand(newCourse.getId()).toUri();
        return ResponseEntity.created(url).body(newCourse);
    }

    // Update course
    @PreAuthorize("hasRole('ADMIN') || hasRole('INSTRUCTOR')")
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable String courseId, @RequestBody @Valid Course course) {
        course.setId(courseId);
        return ResponseEntity.ok(courseRepository.save(course));
    }

    // Delete course
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{courseId}")
    public ResponseEntity deleteCourse(@PathVariable String courseId) {
        courseRepository.deleteById(courseId);
        return ResponseEntity.noContent().build();
    }

    // Additional methods

    // Publish/unpublish course
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{courseId}/publish")
    public ResponseEntity publishCourse(@PathVariable String courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            Course courseToUpdate = course.get();
            courseToUpdate.setPublished(!courseToUpdate.isPublished());
            courseRepository.save(courseToUpdate);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
