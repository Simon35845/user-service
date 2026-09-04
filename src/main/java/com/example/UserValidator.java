package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserValidator {
    private static final Logger log = LoggerFactory.getLogger(UserValidator.class);

    public static boolean validateUserData(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            log.warn("Имя не может быть пустым");
            return false;
        }
        if (email == null || email.isBlank()) {
            log.warn("Email не может быть пустым");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            log.warn("Некорректный формат email: {}", email);
            return false;
        }
        if (age == null) {
            log.warn("Возраст не может быть пустым");
            return false;
        }
        if (age < 0) {
            log.warn("Возраст не может быть отрицательным: {}", age);
            return false;
        }
        if (age > 150) {
            log.warn("Возраст не может превышать 150 лет: {}", age);
            return false;
        }
        return true;
    }
}
