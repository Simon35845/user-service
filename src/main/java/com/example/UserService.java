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
            throw new IllegalArgumentException("Некорректные данные: проверьте имя, email или возраст.");
        }

        try {
            UserEntity entity = new UserEntity(name, email, age);
            userDao.save(entity);
            log.info("Пользователь успешно сохранён: {}", entity);
            return entity;

        } catch (ConstraintViolationException e) {
            log.warn("Email уже существует: {}", email);
            throw new IllegalArgumentException("Пользователь с таким email уже существует.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            throw new RuntimeException("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при сохранении", e);
            throw new RuntimeException("Ошибка при сохранении пользователя.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сохранении", e);
            throw new RuntimeException("Внутренняя ошибка сервера.");
        }
    }

    public UserEntity findUserById(Integer id) {
        log.info("Поиск пользователя по id={}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом.");
        }

        try {
            UserEntity user = userDao.findById(id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден", id);
                throw new IllegalArgumentException("Пользователь с ID " + id + " не найден.");
            }

            log.info("Пользователь найден: {}", user);
            return user;

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);
            throw new IllegalArgumentException("Пользователь с ID " + id + " не найден.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            throw new RuntimeException("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);
            throw new RuntimeException("Ошибка при поиске пользователя.");
        }
    }

    public UserEntity findUserByEmail(String email) {
        log.info("Поиск пользователя по email={}", email);

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым.");
        }

        try {
            UserEntity user = userDao.findByEmail(email);
            if (user == null) {
                log.warn("Пользователь с email {} не найден", email);
                throw new IllegalArgumentException("Пользователь с email " + email + " не найден.");
            }

            log.info("Пользователь найден: {}", user);
            return user;

        } catch (NoResultException e) {
            log.warn("Пользователь с email {} не найден", email);
            throw new IllegalArgumentException("Пользователь с email " + email + " не найден.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            throw new RuntimeException("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);
            throw new RuntimeException("Ошибка при поиске пользователя.");

        }
    }

    public List<UserEntity> findAllUsers() {
        log.info("Поиск всех пользователей");

        try {
            List<UserEntity> users = userDao.findAll();
            log.info("Найдено пользователей: {}", users.size());
            return users;

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            throw new RuntimeException("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);
            throw new RuntimeException("Ошибка при получении списка пользователей.");

        }
    }

    public UserEntity updateUserById(Integer id, String name, String email, Integer age) {
        log.info("Изменение пользователя по id={}, name={}, email={}, age={}", id, name, email, age);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом.");
        }

        if (!UserValidator.validateUserData(name, email, age)) {
            throw new IllegalArgumentException("Некорректные данные: проверьте имя, email или возраст.");
        }

        try {
            UserEntity fromDb = userDao.findById(id);
            if (fromDb == null) {
                log.warn("Пользователь с id={} не найден", id);
                throw new IllegalArgumentException("Пользователь с ID " + id + " не найден.");
            }

            fromDb.setName(name);
            fromDb.setEmail(email);
            fromDb.setAge(age);

            UserEntity updated = userDao.update(fromDb);
            log.info("Пользователь успешно изменён: {}", updated);
            return updated;

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);
            throw new IllegalArgumentException("Пользователь с ID " + id + " не найден.");

        } catch (ConstraintViolationException e) {
            log.warn("Email занят другим пользователем: {}", email);
            throw new IllegalArgumentException("Email " + email + " уже используется другим пользователем.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            throw new RuntimeException("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при обновлении", e);
            throw new RuntimeException("Ошибка при обновлении пользователя.");

        }
    }

    public UserEntity updateUserByEmail(String currentEmail, String newName, String newEmail, Integer newAge) {
        log.info("Изменение пользователя по email={}, name={}, email={}, age={}",
                currentEmail, newName, newEmail, newAge);

        if (currentEmail == null || currentEmail.isBlank()) {
            throw new IllegalArgumentException("Текущий email не может быть пустым.");
        }

        if (!UserValidator.validateUserData(newName, newEmail, newAge)) {
            throw new IllegalArgumentException("Некорректные данные: проверьте имя, email или возраст.");
        }

        try {
            UserEntity fromDb = userDao.findByEmail(currentEmail);
            if (fromDb == null) {
                log.warn("Пользователь с email {} не найден", currentEmail);
                throw new IllegalArgumentException("Пользователь с email " + currentEmail + " не найден.");
            }

            fromDb.setName(newName);
            fromDb.setEmail(newEmail);
            fromDb.setAge(newAge);

            UserEntity updated = userDao.update(fromDb);
            log.info("Пользователь успешно обновлён: {}", updated);
            return updated;

        } catch (NoResultException e) {
            log.warn("Пользователь с email {} не найден", currentEmail);
            throw new IllegalArgumentException("Пользователь с email " + currentEmail + " не найден.");

        } catch (ConstraintViolationException e) {
            log.warn("Email уже занят: {}", newEmail);
            throw new IllegalArgumentException("Email " + newEmail + " уже используется другим пользователем.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            throw new RuntimeException("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при обновлении", e);
            throw new RuntimeException("Ошибка при обновлении пользователя.");

        }
    }

    public void deleteUserById(Integer id) {
        log.info("Удаление пользователя по id={}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом.");
        }

        try {
            UserEntity user = userDao.findById(id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден", id);
                throw new IllegalArgumentException("Пользователь с ID " + id + " не найден.");
            }

            userDao.delete(user);
            log.info("Пользователь с id={} удалён", id);

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);
            throw new IllegalArgumentException("Пользователь с ID " + id + " не найден.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            throw new RuntimeException("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при удалении", e);
            throw new RuntimeException("Ошибка при удалении пользователя.");

        }
    }
}
