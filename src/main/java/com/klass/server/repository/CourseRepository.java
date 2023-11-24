package com.klass.server.repository;

import com.klass.server.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    // Get courses by instructor
    List<Course> findByInstructor(String instructor);

    // Get courses by student (search in students list)
    @Query("{'students': {$regex: ?0}}")
    List<Course> findByStudent(String student);
}
