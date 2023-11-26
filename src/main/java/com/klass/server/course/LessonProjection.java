package com.klass.server.course;

import com.klass.server.activity.ActivityProjectionPreview;

import java.util.List;

public record LessonProjection(
        int order,
        String name,
        String description,
        List<ActivityProjectionPreview> activities) {
}
