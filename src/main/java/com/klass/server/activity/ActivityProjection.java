package com.klass.server.activity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.klass.server.user.UserProjection;
import org.bson.types.ObjectId;

import java.util.List;

public record ActivityProjection(
        String id,
        // TODO aggregation for course name
        @JsonSerialize(using = ToStringSerializer.class)
        ObjectId idCourse,
        String type,
        String name,
        String content,
        boolean enabled,
        List<UserProjection> completedBy,
        List<SubmissionProjection> submissions,
        String openDate,
        String dueDate
) {
}
