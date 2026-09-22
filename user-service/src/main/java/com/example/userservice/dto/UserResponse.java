package com.example.userservice.dto;
/**
 * Класс, который уходит в контроллер
 * @author Yushinova (TATYANA YUSHINOVA)
 */
public class UserResponse {

    private Integer id;

    private String name;

    private String email;

    private Integer age;

    public UserResponse(Integer id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getId() {
        return id;
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

    @Override
    public String toString() {
        return String.format("User{id=%d, name=%s, email=%s, age=%d}", id, name, email, age);
    }
}
