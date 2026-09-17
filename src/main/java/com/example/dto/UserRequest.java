package com.example.dto;

import jakarta.validation.constraints.*;
/**
 * Класс, который приходит из контроллера
 * @author Yushinova (TATYANA YUSHINOVA)
 */
public class UserRequest {
    @NotBlank
    private String name;

    @NotBlank(message = "email обязателен")
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @NotNull(message = "Возраст обязателен")
    @Min(value=0, message = "Возраст должен быть больше 0")
    @Max(value=150, message = "Возраст не может быть больше 150 лет")
    private Integer age;

    public UserRequest(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }
}
