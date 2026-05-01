package com.gabriel.task_manager.Infra.Exception;

// 404 - Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
