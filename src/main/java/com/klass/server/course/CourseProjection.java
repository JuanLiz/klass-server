package com.klass.server.course;

import com.klass.server.user.UserProjection;

import java.util.List;

public record CourseProjection(
        String id,
        String name,
        String slug,
        String description,
        String image,
        String category,
        boolean published,
        UserProjection instructor,
        List<UserProjection> students,
        List<LessonProjection> lessons
) {


}
