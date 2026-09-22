package com.example.userservice.controller;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.exception.UserAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean   // ← новая аннотация
    private UserService userService;


    @Test
    @DisplayName("POST /users — создаёт пользователя и возвращает 201")
    void createUser_ShouldReturn201_WhenDataIsValid() throws Exception {
        // given
        UserRequest request = new UserRequest("Иван", "ivan@mail.ru", 25);
        UserResponse response = new UserResponse(1, "Иван", "ivan@mail.ru", 25);
        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        // when + then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService, times(1)).createUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("POST /users — возвращает 400, если email невалидный")
    void createUser_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        // given
        UserRequest request = new UserRequest("Иван", "invalid-email", 25);

        // when + then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequest.class));
    }

    @Test
    @DisplayName("POST /users — возвращает 409, если email уже занят")
    void createUser_ShouldReturn409_WhenEmailExists() throws Exception {
        // given
        UserRequest request = new UserRequest("Иван", "ivan@mail.ru", 25);
        when(userService.createUser(any(UserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Email занят"));

        // when + then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /users/{id} — возвращает 200 и пользователя")
    void getUserById_ShouldReturn200_WhenUserExists() throws Exception {
        // given
        UserResponse response = new UserResponse(1, "Иван", "ivan@mail.ru", 25);
        when(userService.getUserById(1)).thenReturn(response);

        // when + then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));
    }

    @Test
    @DisplayName("GET /users/{id} — возвращает 404, если пользователь не найден")
    void getUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
        // given
        when(userService.getUserById(999))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));

        // when + then
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users — возвращает 200 и список пользователей")
    void getAllUsers_ShouldReturn200AndList() throws Exception {
        // given
        List<UserResponse> users = List.of(
                new UserResponse(1, "Иван", "ivan@mail.ru", 25),
                new UserResponse(2, "Пётр", "petr@mail.ru", 30)
        );
        when(userService.getAll()).thenReturn(users);

        // when + then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[1].name").value("Пётр"));
    }

    @Test
    @DisplayName("GET /users — возвращает 200 и пустой список")
    void getAllUsers_ShouldReturn200AndEmptyList() throws Exception {
        // given
        when(userService.getAll()).thenReturn(List.of());

        // when + then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("PUT /users/{id} — возвращает 200 и обновлённого пользователя")
    void updateUser_ShouldReturn200_WhenDataIsValid() throws Exception {
        // given
        UserRequest request = new UserRequest("Иван Сидоров", "ivan_new@mail.ru", 35);
        UserResponse response = new UserResponse(1, "Иван Сидоров", "ivan_new@mail.ru", 35);
        when(userService.updateUser(eq(1), any(UserRequest.class))).thenReturn(response);

        // when + then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Сидоров"))
                .andExpect(jsonPath("$.email").value("ivan_new@mail.ru"))
                .andExpect(jsonPath("$.age").value(35));

        verify(userService, times(1)).updateUser(eq(1), any(UserRequest.class));
    }

    @Test
    @DisplayName("PUT /users/{id} — возвращает 404, если пользователь не найден")
    void updateUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        // given
        UserRequest request = new UserRequest("Иван", "ivan@mail.ru", 25);
        when(userService.updateUser(eq(999), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("Пользователь не найден"));

        // when + then
        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/{id} — возвращает 400, если данные невалидны")
    void updateUser_ShouldReturn400_WhenDataIsInvalid() throws Exception {
        // given
        UserRequest request = new UserRequest("", "invalid-email", -5);

        // when + then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /users/{id} — возвращает 204")
    void deleteUser_ShouldReturn204_WhenUserExists() throws Exception {
        // given
        doNothing().when(userService).delete(1);

        // when + then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1);
    }

    @Test
    @DisplayName("DELETE /users/{id} — возвращает 404, если пользователь не найден")
    void deleteUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        // given
        doThrow(new UserNotFoundException("Пользователь не найден"))
                .when(userService).delete(999);

        // when + then
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}