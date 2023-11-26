package com.klass.server.activity;

// Basic preview of an activity

public record ActivityProjectionPreview(
        String id,
        String type,
        String name,
        boolean enabled
) {
}
