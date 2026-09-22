package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * Класс, который приходит из контроллера
 *
 * @author Yushinova (TATYANA YUSHINOVA)
 */
public class UserRequest {
    @Schema(description = "Имя пользователя", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name обязательно")
    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\s-]{2,100}$",
            message = "Имя должно содержать только буквы, пробелы и дефис, от 2 до 100 символов")
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "email обязателен")
    @Email(regexp = ".+@.+\\..+", message = "Введите корректный email, например user@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "25", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Возраст обязателен")
    @Min(value = 0, message = "Возраст должен быть больше 0")
    @Max(value = 150, message = "Возраст не может быть больше 150 лет")
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
