package com.gabriel.task_manager.Infra.Exception;

// 403 - Forbidden
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
