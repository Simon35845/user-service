package com.example.userservice.dto;

import java.util.Map;

/**
 * DTO с информацией об полях, не прошедших валидацию
 *
 * @author Simon35845
 */
public class ValidationErrorResponse {
    private final String message;
    private Map<String, String> errorMap;

    public ValidationErrorResponse(Map<String, String> errorMap) {
        this.message = "Введены некорректные данные";
        this.errorMap = errorMap;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }

    public void setErrorMap(Map<String, String> errorMap) {
        this.errorMap = errorMap;
    }
}
