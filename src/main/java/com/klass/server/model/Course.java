package com.klass.server.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {

    @Id
    private String id;
    private String name;
    private String slug;
    private String description;
    private String image;
    private String category;
    private boolean published;
    private String instructor;
    private List<String> students;
    private List<Lesson> lessons;
}
