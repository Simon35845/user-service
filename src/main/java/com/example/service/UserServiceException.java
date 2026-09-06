package com.example.service;

/**
 * Класс используется для передачи сообщения об ошибке при возникновении исключений в UserService.
 *
 * @author Simon35845
 */
public class UserServiceException extends Exception {
    public UserServiceException(String message) {
        super(message);
    }
}
