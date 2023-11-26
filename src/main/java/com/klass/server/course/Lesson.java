package com.klass.server.course;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    private int order;
    private String name;
    private String description;
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> activities;
}
