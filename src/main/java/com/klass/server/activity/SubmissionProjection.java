package com.klass.server.activity;

import com.klass.server.user.UserProjection;
import org.bson.types.ObjectId;

public record SubmissionProjection(
        UserProjection student,
        String file,
        String grade
) {
}
