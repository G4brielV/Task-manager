package com.gabriel.task_manager.Infra.Exception;

// 400 - Bad Request
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

