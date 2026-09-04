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

    public ServiceResult<UserEntity> createUser(String name, String email, Integer age) {
        log.info("Создание пользователя: name={}, email={}, age={}", name, email, age);

        if (!UserValidator.validateUserData(name, email, age)) {
            return ServiceResult.error("Некорректные данные: проверьте имя, email или возраст.");
        }

        try {
            UserEntity entity = new UserEntity(name, email, age);
            userDao.save(entity);
            log.info("Пользователь успешно сохранён: {}", entity);
            return ServiceResult.success(entity);

        } catch (ConstraintViolationException e) {
            log.warn("Email уже существует: {}", email);
            return ServiceResult.error("Пользователь с таким email уже существует.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            return ServiceResult.error("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при сохранении", e);
            return ServiceResult.error("Ошибка при сохранении пользователя.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при сохранении", e);
            return ServiceResult.error("Внутренняя ошибка сервера.");
        }
    }

    public ServiceResult<UserEntity> findUserById(Integer id) {
        log.info("Поиск пользователя по id={}", id);

        if (id == null || id <= 0) {
            log.warn("Некорректный id={}", id);
            return ServiceResult.error("ID должен быть положительным числом.");
        }

        try {
            UserEntity user = userDao.findById(id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден", id);
                return ServiceResult.error("Пользователь с ID " + id + " не найден.");
            }

            log.info("Пользователь найден: {}", user);
            return ServiceResult.success(user);

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);
            return ServiceResult.error("Пользователь с ID " + id + " не найден.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            return ServiceResult.error("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);
            return ServiceResult.error("Ошибка при поиске пользователя.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
            return ServiceResult.error("Внутренняя ошибка сервера.");
        }
    }

    public ServiceResult<UserEntity> findUserByEmail(String email) {
        log.info("Поиск пользователя по email={}", email);

        if (email == null || email.isBlank()) {
            log.warn("Email не может быть пустым");
            return ServiceResult.error("Email не может быть пустым.");
        }

        try {
            UserEntity user = userDao.findByEmail(email);
            if (user == null) {
                log.warn("Пользователь с email {} не найден", email);
                return ServiceResult.error("Пользователь с email " + email + " не найден.");
            }

            log.info("Пользователь найден: {}", user);
            return ServiceResult.success(user);

        } catch (NoResultException e) {
            log.warn("Пользователь с email {} не найден", email);
            return ServiceResult.error("Пользователь с email " + email + " не найден.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            return ServiceResult.error("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);
            return ServiceResult.error("Ошибка при поиске пользователя.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
            return ServiceResult.error("Внутренняя ошибка сервера.");
        }
    }

    public ServiceResult<List<UserEntity>> findAllUsers() {
        log.info("Поиск всех пользователей");

        try {
            List<UserEntity> users = userDao.findAll();
            log.info("Найдено пользователей: {}", users.size());
            return ServiceResult.success(users);

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            return ServiceResult.error("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске", e);
            return ServiceResult.error("Ошибка при получении списка пользователей.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
            return ServiceResult.error("Внутренняя ошибка сервера.");
        }
    }

    public ServiceResult<UserEntity> updateUserById(Integer id, String name, String email, Integer age) {
        log.info("Изменение пользователя по id={}, name={}, email={}, age={}", id, name, email, age);

        if (id == null || id <= 0) {
            log.warn("Некорректный id={}", id);
            return ServiceResult.error("ID должен быть положительным числом.");
        }

        if (!UserValidator.validateUserData(name, email, age)) {
            return ServiceResult.error("Некорректные данные: проверьте имя, email или возраст.");
        }

        try {
            UserEntity fromDb = userDao.findById(id);
            if (fromDb == null) {
                log.warn("Пользователь с id={} не найден", id);
                return ServiceResult.error("Пользователь с ID " + id + " не найден.");
            }

            fromDb.setName(name);
            fromDb.setEmail(email);
            fromDb.setAge(age);

            UserEntity updated = userDao.update(fromDb);
            log.info("Пользователь успешно изменён: {}", updated);
            return ServiceResult.success(updated);

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);
            return ServiceResult.error("Пользователь с ID " + id + " не найден.");

        } catch (ConstraintViolationException e) {
            log.warn("Email занят другим пользователем: {}", email);
            return ServiceResult.error("Email " + email + " уже используется другим пользователем.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            return ServiceResult.error("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при обновлении", e);
            return ServiceResult.error("Ошибка при обновлении пользователя.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обновлении", e);
            return ServiceResult.error("Внутренняя ошибка сервера.");
        }
    }

    public ServiceResult<UserEntity> updateUserByEmail(String currentEmail, String newName, String newEmail, Integer newAge) {
        log.info("Изменение пользователя по email={}, name={}, email={}, age={}",
                currentEmail, newName, newEmail, newAge);

        if (currentEmail == null || currentEmail.isBlank()) {
            log.warn("Текущий email не может быть пустым");
            return ServiceResult.error("Текущий email не может быть пустым.");
        }

        if (!UserValidator.validateUserData(newName, newEmail, newAge)) {
            return ServiceResult.error("Некорректные данные: проверьте имя, email или возраст.");
        }

        try {
            UserEntity fromDb = userDao.findByEmail(currentEmail);
            if (fromDb == null) {
                log.warn("Пользователь с email {} не найден", currentEmail);
                return ServiceResult.error("Пользователь с email " + currentEmail + " не найден.");
            }

            fromDb.setName(newName);
            fromDb.setEmail(newEmail);
            fromDb.setAge(newAge);

            UserEntity updated = userDao.update(fromDb);
            log.info("Пользователь успешно обновлён: {}", updated);
            return ServiceResult.success(updated);

        } catch (NoResultException e) {
            log.warn("Пользователь с email {} не найден", currentEmail);
            return ServiceResult.error("Пользователь с email " + currentEmail + " не найден.");

        } catch (ConstraintViolationException e) {
            log.warn("Email уже занят: {}", newEmail);
            return ServiceResult.error("Email " + newEmail + " уже используется другим пользователем.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            return ServiceResult.error("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при обновлении", e);
            return ServiceResult.error("Ошибка при обновлении пользователя.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обновлении", e);
            return ServiceResult.error("Внутренняя ошибка сервера.");
        }
    }

    public ServiceResult<Boolean> deleteUserById(Integer id) {
        log.info("Удаление пользователя по id={}", id);

        if (id == null || id <= 0) {
            log.warn("Некорректный id={}", id);
            return ServiceResult.error("ID должен быть положительным числом.");
        }

        try {
            UserEntity user = userDao.findById(id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден", id);
                return ServiceResult.error("Пользователь с ID " + id + " не найден.");
            }

            userDao.delete(user);
            log.info("Пользователь с id={} удалён", id);
            return ServiceResult.success(true);

        } catch (NoResultException e) {
            log.warn("Пользователь с id={} не найден", id);
            return ServiceResult.error("Пользователь с ID " + id + " не найден.");

        } catch (JDBCException e) {
            log.error("База данных недоступна", e);
            return ServiceResult.error("Сервис временно недоступен. Попробуйте позже.");

        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при удалении", e);
            return ServiceResult.error("Ошибка при удалении пользователя.");

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при удалении", e);
            return ServiceResult.error("Внутренняя ошибка сервера.");
        }
    }
}
