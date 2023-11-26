package com.klass.server.activity;

import com.klass.server.user.UserProjection;

import java.util.List;

public record ActivityProjection(
        String id,
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
