package com.example;

import com.example.console.ConsoleHelper;
import com.example.console.UserConsoleController;
import com.example.console.UserValidator;
import com.example.dao.UserDao;
import com.example.service.UserService;
import com.example.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный класс приложения. В методе main происходит создания компонентов и запуск приложения.
 *
 * @author Simon35845
 * @author FlameFlow2001 (Shundev Kirill)
 */
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
