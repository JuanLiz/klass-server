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
@Document(collection = "activities")
public class Activity {
    @Id
    private String id;
    private String type;
    private String name;
    private String content;
    private boolean enabled;
    private List<String> completedBy;
    private List<Submission> submissions;
    private String openDate;
    private String dueDate;

}
