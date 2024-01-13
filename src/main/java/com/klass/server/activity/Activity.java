package com.klass.server.activity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activities")
public class Activity {

    // TODO add validations
    @Id
    private String id;
    private String type;
    private String name;
    private String content;
    private boolean enabled;
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> completedBy = new ArrayList<>();
    private List<Submission> submissions = new ArrayList<>();
    private String openDate;
    private String dueDate;

}
