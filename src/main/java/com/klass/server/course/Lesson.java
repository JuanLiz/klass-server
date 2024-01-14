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

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {

    @NotNull(message = "Order cannot be null")
    private int order;

    @NotBlank(message = "Name cannot be blank or null")
    private String name;

    private String description;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> activities;
}
