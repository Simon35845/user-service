package com.example;

import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {

    private final UserDao userDao;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserEntity createUser(String name, String email, Integer age) throws UserServiceException {
        log.info("Создание пользователя: name={}, email={}, age={}.", name, email, age);
        try {
            if (userDao.existsByEmail(email)) {
                log.warn("Пользователь с email={} уже существует.", email);
                throw new IllegalArgumentException("Пользователь с email=%s уже существует.".formatted(email));
            }

            UserEntity userToSave = new UserEntity(name, email, age);
            UserEntity savedUser = userDao.save(userToSave);
            log.info("Пользователь успешно сохранён: {}.", savedUser);
            return savedUser;
        } catch (IllegalArgumentException e) {
            throw new UserServiceException(e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя с email={}.", email, e);
            throw new UserServiceException("Внутренняя ошибка сервера.");
        }
    }

    public UserEntity findUserById(Integer id) throws UserServiceException {
        log.info("Поиск пользователя по id={}.", id);
        try {
            UserEntity user = userDao.findById(id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден.", id);
                throw new IllegalArgumentException("Пользователь с id=%d не найден.".formatted(id));
            }

            log.info("Пользователь найден: {}.", user);
            return user;
        } catch (IllegalArgumentException e) {
            throw new UserServiceException(e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя по id={}.", id, e);
            throw new UserServiceException("Внутренняя ошибка сервера.");
        }
    }

    public UserEntity findUserByEmail(String email) throws UserServiceException {
        log.info("Поиск пользователя по email={}", email);
        try {
            UserEntity user = userDao.findByEmail(email);
            log.info("Пользователь найден: {}.", user);
            return user;
        } catch (NoResultException e) {
            log.warn("Пользователь с email={} не найден.", email);
            throw new UserServiceException("Пользователь с email=%s не найден.".formatted(email));
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя по email={}.", email, e);
            throw new UserServiceException("Внутренняя ошибка сервера.");
        }
    }

    public List<UserEntity> getAllUsers() throws UserServiceException {
        log.info("Вывод всех пользователей.");
        try {
            List<UserEntity> users = userDao.findAll();
            log.info("Найдено {} пользователей: .", users.size());
            return users;
        } catch (Exception e) {
            log.error("Ошибка при выводе всех пользователей.", e);
            throw new UserServiceException("Внутренняя ошибка сервера.");
        }
    }

    public UserEntity updateUserById(Integer id, String name, String email, Integer age) throws UserServiceException {
        log.info("Изменение пользователя: id={} name={}, email={}, age={}.", id, name, email, age);
        try {
            UserEntity user = userDao.findById(id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден.", id);
                throw new IllegalArgumentException("Пользователь с id=%d не найден.".formatted(id));
            }
            if (!user.getEmail().equals(email) && userDao.existsByEmail(email)) {
                log.warn("Пользователь с email={} уже существует.", email);
                throw new IllegalArgumentException("Пользователь с email=%s уже существует.".formatted(email));
            }

            user.setName(name);
            user.setEmail(email);
            user.setAge(age);
            UserEntity updatedUser = userDao.update(user);

            log.info("Пользователь успешно изменён: {}.", updatedUser);
            return updatedUser;
        } catch (IllegalArgumentException e) {
            throw new UserServiceException(e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при изменении пользователя с id={}.", id, e);
            throw new UserServiceException("Внутренняя ошибка сервера.");
        }
    }

    public void deleteUserById(Integer id) throws UserServiceException {
        try {
            UserEntity user = userDao.findById(id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден.", id);
                throw new IllegalArgumentException("Пользователь с id=%d не найден.".formatted(id));
            }

            userDao.delete(user);
            log.info("Пользователь с id={} удалён.", id);
        } catch (IllegalArgumentException e) {
            throw new UserServiceException(e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с id={}.", id, e);
            throw new UserServiceException("Внутренняя ошибка сервера.");
        }
    }
}
