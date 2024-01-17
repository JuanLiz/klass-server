package com.klass.server.activity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Document(collection = "activities")
public class Activity {

    // TODO add idCourse to existing activities
    @Id
    private String id;

    // Prevent returning as timestamp
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "A course ID must be provided")
    private ObjectId idCourse;

    @NotBlank(message = "Activity type cannot be blank or null")
    @Pattern(regexp = "text|video|pdf|assign", message = "Activity type must be text, video, pdf or assign")
    private String type;

    @NotBlank(message = "Activity name cannot be blank or null")
    private String name;

    @NotBlank(message = "Content cannot be blank or null")
    private String content;

    private boolean enabled = true;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> completedBy = new ArrayList<>();

    private List<Submission> submissions = new ArrayList<>();

    private String openDate;

    private String dueDate;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
