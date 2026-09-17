package com.example.service;

import com.example.entity.UserEntity;
import com.example.exception.UserServiceException;
import jakarta.persistence.NoResultException;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock
//    private UserDao userDao;
//
//    @InjectMocks
//    private UserService userService;
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void createUser_ifValid() throws UserServiceException {
//        String name = "Dave";
//        String email = "dave@example.com";
//        Integer age = 40;
//
//        UserEntity savedUser = new UserEntity(name, email, age);
//        savedUser.setId(1);
//        when(userDao.save(any(UserEntity.class))).thenReturn(savedUser);
//
//        UserEntity result = userService.createUser(name, email, age);
//
//        assertNotNull(result);
//        assertEquals(1, result.getId());
//        assertEquals(name, result.getName());
//        assertEquals(email, result.getEmail());
//        assertEquals(age, result.getAge());
//        verify(userDao).save(any(UserEntity.class));
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void createUser_ifEmailAlreadyExists() {
//        String name = "Dave";
//        String email = "alice@example.com";
//        Integer age = 40;
//
//        when(userDao.save(any(UserEntity.class)))
//                .thenThrow(new ConstraintViolationException("duplicate key", null, "users_email_key"));
//
//        UserServiceException exception = assertThrows(
//                UserServiceException.class,
//                () -> userService.createUser(name, email, age)
//        );
//        assertEquals(
//                "Пользователь с email=alice@example.com уже существует.",
//                exception.getMessage()
//        );
//        verify(userDao).save(any(UserEntity.class));
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void createUser_ifDatabaseError() {
//        String name = "Dave";
//        String email = "dave@example.com";
//        Integer age = 40;
//
//        when(userDao.save(any(UserEntity.class)))
//                .thenThrow(new HibernateException("Database error"));
//
//        UserServiceException exception = assertThrows(
//                UserServiceException.class,
//                () -> userService.createUser(name, email, age)
//        );
//        assertEquals("Внутренняя ошибка сервера.", exception.getMessage());
//        verify(userDao).save(any(UserEntity.class));
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void updateUserById_ifUserExists() throws UserServiceException {
//        Integer id = 1;
//        UserEntity existingUser = new UserEntity("Alice", "alice@example.com", 24);
//        existingUser.setId(id);
//        when(userDao.findById(id)).thenReturn(existingUser);
//
//        UserEntity updatedUser = new UserEntity("Alice Updated", "alice.new@example.com", 25);
//        updatedUser.setId(id);
//        when(userDao.update(any(UserEntity.class))).thenReturn(updatedUser);
//
//        UserEntity result = userService.updateUserById(id, "Alice Updated", "alice.new@example.com", 25);
//
//        assertNotNull(result);
//        assertEquals(id, result.getId());
//        assertEquals("Alice Updated", result.getName());
//        assertEquals("alice.new@example.com", result.getEmail());
//        assertEquals(25, result.getAge());
//        verify(userDao).findById(id);
//        verify(userDao).update(any(UserEntity.class));
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void updateUserById_ifUserNotExists() {
//        Integer id = 99;
//        when(userDao.findById(id)).thenReturn(null);
//
//        UserServiceException exception = assertThrows(
//                UserServiceException.class,
//                () -> userService.updateUserById(id, "Ghost", "ghost@example.com", 30)
//        );
//        assertEquals("Пользователь с id=99 не найден.", exception.getMessage());
//        verify(userDao).findById(id);
//        verify(userDao, never()).update(any(UserEntity.class));
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void updateUserById_ifEmailAlreadyExists() {
//        Integer id = 1;
//        UserEntity existingUser = new UserEntity("Alice", "alice@example.com", 24);
//        existingUser.setId(id);
//        when(userDao.findById(id)).thenReturn(existingUser);
//
//        when(userDao.update(any(UserEntity.class)))
//                .thenThrow(new ConstraintViolationException("duplicate key", null, "users_email_key"));
//
//        UserServiceException exception = assertThrows(
//                UserServiceException.class,
//                () -> userService.updateUserById(id, "Alice", "bob@example.com", 24)
//        );
//        assertEquals(
//                "Пользователь с email=bob@example.com уже существует.",
//                exception.getMessage()
//        );
//        verify(userDao).findById(id);
//        verify(userDao).update(any(UserEntity.class));
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void updateUserById_ifDatabaseError() {
//        Integer id = 1;
//        when(userDao.findById(id)).thenThrow(new HibernateException("Database error"));
//
//        UserServiceException exception = assertThrows(
//                UserServiceException.class,
//                () -> userService.updateUserById(id, "Alice", "alice@example.com", 24)
//        );
//        assertEquals("Внутренняя ошибка сервера.", exception.getMessage());
//        verify(userDao).findById(id);
//        verify(userDao, never()).update(any(UserEntity.class));
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void findUserById_ifUserExists() {
//        Integer id = 1;
//        UserEntity user = new UserEntity("Alice", "alice@example.com", 24);
//        when(userDao.findById(id)).thenReturn(user);
//
//        UserEntity expectedUser = user;
//        UserEntity actualUser = null;
//        try {
//            actualUser = userService.findUserById(id);
//        } catch (UserServiceException e) {
//            System.out.println(e.getMessage());
//        }
//
//        assertEquals(expectedUser.getName(), actualUser.getName());
//        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
//        assertEquals(expectedUser.getAge(), actualUser.getAge());
//        verify(userDao).findById(id);
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void findUserById_ifUserNotExists() {
//        Integer id = 2;
//        when(userDao.findById(id)).thenReturn(null);
//
//        Exception exception = assertThrows(UserServiceException.class, () -> userService.findUserById(id));
//        String expectedMessage = "Пользователь с id=%d не найден.".formatted(id);
//        assertEquals(expectedMessage, exception.getMessage());
//        verify(userDao).findById(id);
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void findUserById_ifExceptionThrows() {
//        Integer id = 3;
//        when(userDao.findById(id)).thenThrow(new RuntimeException());
//
//        Exception exception = assertThrows(UserServiceException.class, () -> userService.findUserById(id));
//        String expectedMessage = "Внутренняя ошибка сервера.";
//        assertEquals(expectedMessage, exception.getMessage());
//        verify(userDao).findById(id);
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void getAllUsers_ifUsersExists() {
//        UserEntity user1 = new UserEntity("Alice", "alice@example.com", 24);
//        UserEntity user2 = new UserEntity("Bob", "bob@example.com", 31);
//        List<UserEntity> users = List.of(user1, user2);
//        when(userDao.findAll()).thenReturn(users);
//
//        List<UserEntity> expectedUsers = users;
//        List<UserEntity> actualUsers = null;
//        try {
//            actualUsers = userService.getAllUsers();
//        } catch (UserServiceException e) {
//            System.out.println(e.getMessage());
//        }
//
//        assertIterableEquals(expectedUsers, actualUsers);
//        verify(userDao).findAll();
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void getAllUsers_ifUsersNotExists() {
//        List<UserEntity> users = Collections.emptyList();
//        when(userDao.findAll()).thenReturn(users);
//
//        List<UserEntity> expectedUsers = users;
//        List<UserEntity> actualUsers = null;
//        try {
//            actualUsers = userService.getAllUsers();
//        } catch (UserServiceException e) {
//            System.out.println(e.getMessage());
//        }
//
//        assertIterableEquals(expectedUsers, actualUsers);
//        verify(userDao).findAll();
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void getAllUsers_ifExceptionThrows() {
//        when(userDao.findAll()).thenThrow(new RuntimeException());
//
//        Exception exception = assertThrows(UserServiceException.class, () -> userService.getAllUsers());
//        String expectedMessage = "Внутренняя ошибка сервера.";
//        assertEquals(expectedMessage, exception.getMessage());
//        verify(userDao).findAll();
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void findUserByEmail_ifUsersExists() throws UserServiceException {
//        UserEntity user = new UserEntity("test", "test@test.ru", 18);
//        when(userDao.findByEmail("test@test.ru")).thenReturn(user);
//        UserEntity result = userService.findUserByEmail("test@test.ru");
//        assertNotNull(result);
//        assertEquals("test@test.ru", result.getEmail());
//        verify(userDao).findByEmail("test@test.ru");
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void findUserByEmail_ifUserNotFoundTest() {
//        String email = "test@test.ru";
//        when(userDao.findByEmail(email)).thenThrow(new NoResultException());
//        UserServiceException ex = assertThrows(
//                UserServiceException.class, () -> userService.findUserByEmail(email)
//        );
//        assertEquals(
//                "Пользователь с email=test@test.ru не найден.",
//                ex.getMessage()
//        );
//        verify(userDao).findByEmail(email);
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void findUserByEmail_ifDatabaseError() {
//        String email = "test@test.ru";
//        when(userDao.findByEmail(email))
//                .thenThrow(new HibernateException("Database error"));
//        UserServiceException exception = assertThrows(
//                UserServiceException.class,
//                () -> userService.findUserByEmail(email)
//        );
//        assertEquals(
//                "Внутренняя ошибка сервера.",
//                exception.getMessage()
//        );
//        verify(userDao).findByEmail(email);
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void deleteUserById_ifUsersExists() throws UserServiceException {
//        UserEntity user = new UserEntity("test", "test@test.ru", 18);
//        user.setId(1);
//        when(userDao.findById(1)).thenReturn(user);
//        userService.deleteUserById(1);
//        verify(userDao).delete(user);
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void deleteUserById_ifUserNotFoundTest() {
//        Integer id = 1;
//        when(userDao.findById(id)).thenReturn(null);
//        UserServiceException ex = assertThrows(
//                UserServiceException.class, () -> userService.findUserById(id)
//        );
//        assertEquals(
//                "Пользователь с id=1 не найден.",
//                ex.getMessage()
//        );
//        verify(userDao).findById(id);
//        verify(userDao, never()).delete(any(UserEntity.class));
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void deleteUserById_ifDatabaseError() {
//        Integer id = 1;
//        when(userDao.findById(id))
//                .thenThrow(new HibernateException("Database error"));
//        UserServiceException exception = assertThrows(
//                UserServiceException.class,
//                () -> userService.deleteUserById(id)
//        );
//        assertEquals(
//                "Внутренняя ошибка сервера.",
//                exception.getMessage()
//        );
//        verify(userDao).findById(id);
//        verify(userDao, never()).delete(any(UserEntity.class));
//    }
//}