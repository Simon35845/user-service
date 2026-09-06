package com.example;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = HibernateUtil.getSessionFactory()) {
            UserDao userDao = new UserDao(sessionFactory);
            UserService userService = new UserService(userDao);
            UserValidator userValidator = new UserValidator();
            ConsoleHelper consoleHelper = new ConsoleHelper();
            UserConsoleController userConsoleController = new UserConsoleController(userService, userValidator, consoleHelper);
            userConsoleController.start();
        } catch (Exception e) {
            log.error("Ошибка подключения к БД", e);
        }
    }
}
