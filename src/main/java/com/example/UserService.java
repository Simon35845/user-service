package com.example;

import jakarta.persistence.NoResultException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {

    private final UserDao userDao;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserEntity createUser(String name, String email, Integer age) {

        log.info("Создание пользователя: name={}, email={}, age={}", name, email, age);

        if (!UserValidator.validateUserData(name, email, age)) {
            return null;
        }

        try {
            UserEntity entity = new UserEntity(name, email, age);

            userDao.save(entity);

            log.info("Пользователь успешно сохранён: {}", entity);

            return entity;

        } catch (ConstraintViolationException e) {
            log.warn("Email уже существует: {}", email);

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при сохранении", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сохранении", e);
        }

        return null;
    }

    public UserEntity updateUserById(
            Integer id,
            String name,
            String email,
            Integer age) {

        log.info("Изменение пользователя по id={}, name={}, email={}, age={}", id, name, email, age);

        if (id == null || id <= 0) {
            log.warn("Некорректный id={}", id);
            return null;
        }

        if (!UserValidator.validateUserData(name, email, age)) {
            return null;
        }

        try {
            UserEntity fromDb = userDao.findById(id);

            if (fromDb == null) {
                log.warn("Пользователь с id={} не найден", id);
                return null;
            }

            fromDb.setName(name);
            fromDb.setEmail(email);
            fromDb.setAge(age);

            UserEntity updated = userDao.update(fromDb);

            log.info("Пользователь успешно изменён: {}", updated);

            return updated;

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);

        } catch (ConstraintViolationException e) {
            log.warn("Email занят другим пользователем: {}", email);

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при update", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при update", e);
        }

        return null;
    }

    public UserEntity updateUserByEmail(
            String currentEmail,
            String newName,
            String newEmail,
            Integer newAge) {

        log.info(
                "Изменение пользователя по email={}, name={}, email={}, age={}",
                currentEmail,
                newName,
                newEmail,
                newAge
        );

        if (currentEmail == null || currentEmail.isBlank()) {
            log.warn("Текущий email не может быть пустым");
            return null;
        }

        if (!UserValidator.validateUserData(newName, newEmail, newAge)) {
            return null;
        }

        try {
            UserEntity fromDb = userDao.findByEmail(currentEmail);

            if (fromDb == null) {
                log.warn("Пользователь с email {} не найден", currentEmail);
                return null;
            }

            fromDb.setName(newName);
            fromDb.setEmail(newEmail);
            fromDb.setAge(newAge);

            UserEntity updated = userDao.update(fromDb);

            log.info("Пользователь успешно обновлён: {}", updated);

            return updated;

        } catch (NoResultException e) {
            log.warn("Пользователь с email {} не найден", currentEmail);

        } catch (ConstraintViolationException e) {
            log.warn(
                    "Email уже занят: {}",
                    newEmail
            );

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при update", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при update", e);
        }

        return null;
    }


    public boolean deleteUserById(Integer id) {

        log.info("Удаление пользователя по id={}", id);

        if (id == null || id <= 0) {
            log.warn("Некорректный id={}", id);
            return false;
        }

        try {
            UserEntity user = userDao.findById(id);

            if (user == null) {
                log.warn("Пользователь с id={} не найден", id);
                return false;
            }

            userDao.delete(user);

            log.info("Пользователь с id={} удалён", id);

            return true;

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при удалении", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при удалении", e);
        }

        return false;
    }

    public UserEntity findUserById(Integer id) {

        log.info("Поиск пользователя по id={}", id);

        if (id == null || id <= 0) {
            log.warn("Некорректный id={}", id);
            return null;
        }

        try {
            UserEntity user = userDao.findById(id);

            if (user == null) {
                log.warn("Пользователь с id={} не найден", id);
                return null;
            }

            log.info("Пользователь найден: {}", user);

            return user;

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
        }

        return null;
    }

    public UserEntity findUserByEmail(String email) {

        log.info("Поиск пользователя по email={}", email);

        if (email == null || email.isBlank()) {
            log.warn("Email не может быть пустым");
            return null;
        }

        try {
            UserEntity user = userDao.findByEmail(email);

            if (user == null) {
                log.warn("Пользователь с email {} не найден", email);
                return null;
            }

            log.info("Пользователь найден: {}", user);

            return user;

        } catch (NoResultException e) {
            log.warn("Пользователь с email {} не найден", email);

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
        }

        return null;
    }

    public List<UserEntity> findAllUsers() {

        log.info("Поиск всех пользователей");

        try {
            List<UserEntity> users = userDao.findAll();

            log.info("Найдено пользователей: {}", users.size());

            return users;

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);

        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
        }

        return List.of();
    }
}
