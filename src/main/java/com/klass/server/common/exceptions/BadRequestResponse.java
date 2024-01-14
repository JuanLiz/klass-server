package com.klass.server.common.exceptions;

public record BadRequestResponse(int status, String message) {
}
