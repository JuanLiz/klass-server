package com.klass.server.course;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Name cannot be blank or null")
    private String name;

    @NotBlank(message = "Slug cannot be blank or null")
    private String slug;

    @NotBlank(message = "Description cannot be blank or null")
    private String description;

    private String image;

    private String category;

    private boolean published;

    @NotNull(message = "An instructor ID must be provided")
    // Prevent returning as timestamp
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId instructor;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> students;

    // TODO avoid exception when returning empty list
    private List<Lesson> lessons;
}
