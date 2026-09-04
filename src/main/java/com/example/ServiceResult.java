package com.example;

public class ServiceResult<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;

    private ServiceResult(boolean success, T data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(true, data, null);
    }

    public static <T> ServiceResult<T> error(String message) {
        return new ServiceResult<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
