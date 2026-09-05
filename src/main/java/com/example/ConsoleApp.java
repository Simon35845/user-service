package com.example;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConsoleApp {

    private static final Logger log = LoggerFactory.getLogger(ConsoleApp.class);
    private static UserService userService;
    private static ConsoleView view;

    public static void main(String[] args) {

        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            UserDao userDao = new UserDao(sessionFactory);
            userService = new UserService(userDao);
            view = new ConsoleView();

            while (true) {
                view.showMenu();
                int userChoice = view.readChoice();

                switch (userChoice) {
                    case 1 -> createUser();
                    case 2 -> findUserById();
                    case 3 -> findUserByEmail();
                    case 4 -> showAllUsers();
                    case 5 -> updateUserById();
                    case 6 -> updateUserByEmail();
                    case 7 -> deleteUserById();
                    case 0 -> {
                        System.out.println("\n========================================");
                        System.out.println("  До свидания!");
                        System.out.println("========================================");
                        view.closeScanner();
                        return;
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            }
        } catch (Exception e) {
            log.error("Ошибка подключения к БД", e);
        }
    }



    private static void createUser() {
        System.out.println("\n--- СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ ---");

        try {
            String name = view.readString("Введите имя: ");
            if (name.isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                return;
            }

            String email = view.readString("Введите email: ");
            if (email.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            Integer age = view.readInteger("Введите возраст: ");

            if (age < 0 || age > 200) {
                System.out.println("Возраст должен быть от 0 до 199 лет!");
                return;
            }

            UserEntity user = userService.createUser(name, email, age);
            System.out.println("Создан: " + user);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.println("\n--- ПОИСК ПО ID ---");

        try {
            Integer id = view.readInteger("Введите ID: ");
            UserEntity user = userService.findUserById(id);
            System.out.println("Найден: " + user);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void findUserByEmail() {
        System.out.println("\n--- ПОИСК ПО EMAIL ---");

        try {
            String email = view.readString("Введите email: ");
            if (email.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            UserEntity user = userService.findUserByEmail(email);
            System.out.println("Найден: " + user);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showAllUsers() {
        System.out.println("\n--- ВСЕ ПОЛЬЗОВАТЕЛИ ---");

        try {
            List<UserEntity> users = userService.findAllUsers();
            view.printUsers(users);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateUserById() {
        System.out.println("\n--- ОБНОВЛЕНИЕ ПО ID ---");

        try {
            Integer id = view.readInteger("Введите ID пользователя для обновления: ");

            UserEntity existing = userService.findUserById(id);
            System.out.println("Текущие данные: " + existing);

            String name = view.readString("Введите новое имя: ");
            if (name.isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                return;
            }

            String email = view.readString("Введите новый email: ");
            if (email.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            Integer age = view.readInteger("Введите новый возраст: ");

            UserEntity updated = userService.updateUserById(id, name, email, age);
            System.out.println("Обновлён: " + updated);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateUserByEmail() {
        System.out.println("\n--- ОБНОВЛЕНИЕ ПО EMAIL ---");

        try {
            String currentEmail = view.readString("Введите текущий email пользователя: ");
            if (currentEmail.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            UserEntity existing = userService.findUserByEmail(currentEmail);
            System.out.println("Текущие данные: " + existing);

            String newName = view.readString("Введите новое имя: ");
            if (newName.isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                return;
            }

            String newEmail = view.readString("Введите новый email: ");
            if (newEmail.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            Integer newAge = view.readInteger("Введите новый возраст: ");

            UserEntity updated = userService.updateUserByEmail(currentEmail, newName, newEmail, newAge);
            System.out.println("Обновлён: " + updated);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteUserById() {
        System.out.println("\n--- УДАЛЕНИЕ ПО ID ---");

        try {
            Integer id = view.readInteger("Введите ID пользователя для удаления: ");

            UserEntity user = userService.findUserById(id);
            System.out.println("Будет удалён: " + user);

            System.out.print("Подтвердите удаление (y/n): ");
            String confirm = view.readString("");
            if (confirm.equals("y") || confirm.equals("yes")) {
                userService.deleteUserById(id);
                System.out.println("Пользователь с ID " + id + " удалён.");
            } else {
                System.out.println("Удаление отменено.");
            }

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}