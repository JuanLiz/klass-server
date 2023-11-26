package com.klass.server.course;

import com.klass.server.course.Course;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    // Get courses by instructor
    List<Course> findByInstructor(ObjectId instructor);

    // Get courses by student (search in students list)
    @Query(value = "{ 'students' : ?0 }")
    List<Course> findByStudent(ObjectId student);

}
