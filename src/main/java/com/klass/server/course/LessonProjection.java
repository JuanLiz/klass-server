package com.klass.server.course;

import com.klass.server.activity.ActivityProjectionPreview;

import java.util.List;
import java.util.Optional;

public record LessonProjection(
        Optional<Integer> order,
        Optional<String> name,
        Optional<String> description,
        List<ActivityProjectionPreview> activities) {

}
