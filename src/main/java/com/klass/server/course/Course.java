package com.klass.server.course;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {

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

    private boolean published = false;

    // Prevent returning as timestamp
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId instructor;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> students = new ArrayList<>();

    private List<Lesson> lessons = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
