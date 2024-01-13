package com.klass.server.common.exceptions;

public record BadRequest(int status, String message) {
}
