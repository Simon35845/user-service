package com.example;

public class UserValidator {

    public boolean isIdValid(Integer id) {
        if (id == null) {
            System.out.println("id не может быть пустым.");
            return false;
        }
        if (id <= 0) {
            System.out.println("Id должен быть больше 0.");
            return false;
        }
        return true;
    }

    public boolean isNameValid(String name) {
        if (name == null || name.isBlank()) {
            System.out.println("Имя не может быть пустым.");
            return false;
        }
        return true;
    }

    public boolean isEmailValid(String email) {
        if (email == null || email.isBlank()) {
            System.out.println("Email не может быть пустым.");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("Некорректный формат email.");
            return false;
        }
        return true;
    }

    public boolean isAgeValid(Integer age) {
        if (age == null) {
            System.out.println("Возраст не может быть пустым.");
            return false;
        }
        if (age < 0 || age > 200) {
            System.out.println("Возраст не может быть меньше 0 или больше 200 лет.");
            return false;
        }
        return true;
    }
}
