package com.klass.server.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

// Projected user data, used for GET requests (hide password and other sensitive data)
public record UserProjection(
        @JsonSerialize(using = ToStringSerializer.class)
        ObjectId id,
        String role,
        String name,
        String lastName,
        String email,
        String picture
) {
}
