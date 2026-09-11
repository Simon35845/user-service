package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    /**
     * @author Simon35845
     */
    @Test
    void findUserById_ifUserExists() {
        Integer id = 1;
        UserEntity user = new UserEntity("Alice", "alice@example.com", 24);
        when(userDao.findById(id)).thenReturn(user);

        UserEntity expectedUser = user;
        UserEntity actualUser = null;
        try {
            actualUser = userService.findUserById(id);
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getAge(), actualUser.getAge());
        verify(userDao).findById(id);
    }

    /**
     * @author Simon35845
     */
    @Test
    void findUserById_ifUserNotExists() {
        Integer id = 2;
        when(userDao.findById(id)).thenReturn(null);

        Exception exception = assertThrows(UserServiceException.class, () -> userService.findUserById(id));
        String expectedMessage = "Пользователь с id=%d не найден.".formatted(id);
        assertEquals(expectedMessage, exception.getMessage());
        verify(userDao).findById(id);
    }

    /**
     * @author Simon35845
     */
    @Test
    void findUserById_ifExceptionThrows() {
        Integer id = 3;
        when(userDao.findById(id)).thenThrow(new RuntimeException());

        Exception exception = assertThrows(UserServiceException.class, () -> userService.findUserById(id));
        String expectedMessage = "Внутренняя ошибка сервера.";
        assertEquals(expectedMessage, exception.getMessage());
        verify(userDao).findById(id);
    }

    /**
     * @author Simon35845
     */
    @Test
    void getAllUsers_ifUsersExists() {
        UserEntity user1 = new UserEntity("Alice", "alice@example.com", 24);
        UserEntity user2 = new UserEntity("Bob", "bob@example.com", 31);
        List<UserEntity> users = List.of(user1, user2);
        when(userDao.findAll()).thenReturn(users);

        List<UserEntity> expectedUsers = users;
        List<UserEntity> actualUsers = null;
        try {
            actualUsers = userService.getAllUsers();
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }

        assertIterableEquals(expectedUsers, actualUsers);
        verify(userDao).findAll();
    }

    /**
     * @author Simon35845
     */
    @Test
    void getAllUsers_ifUsersNotExists() {
        List<UserEntity> users = Collections.emptyList();
        when(userDao.findAll()).thenReturn(users);

        List<UserEntity> expectedUsers = users;
        List<UserEntity> actualUsers = null;
        try {
            actualUsers = userService.getAllUsers();
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }

        assertIterableEquals(expectedUsers, actualUsers);
        verify(userDao).findAll();
    }

    /**
     * @author Simon35845
     */
    @Test
    void getAllUsers_ifExceptionThrows() {
        when(userDao.findAll()).thenThrow(new RuntimeException());

        Exception exception = assertThrows(UserServiceException.class, () -> userService.getAllUsers());
        String expectedMessage = "Внутренняя ошибка сервера.";
        assertEquals(expectedMessage, exception.getMessage());
        verify(userDao).findAll();
    }
}