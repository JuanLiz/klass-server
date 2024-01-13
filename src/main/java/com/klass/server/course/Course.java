package com.klass.server.course;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {

    // TODO add validations
    @Id
    private String id;
    private String name;
    private String slug;
    private String description;
    private String image;
    private String category;
    private boolean published;
    // Prevent returning as timestamp
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId instructor;
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> students;
    // TODO avoid exception when returning empty list
    private List<Lesson> lessons;
}
