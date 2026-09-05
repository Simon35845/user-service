package com.example;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private static final Logger log = LoggerFactory.getLogger(ConsoleApp.class);
    private static UserService userService;
    private static Scanner scanner;

    public static void main(String[] args) {

        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            UserDao userDao = new UserDao(sessionFactory);
            userService = new UserService(userDao);
            scanner = new Scanner(System.in);
            while (true){
                showMenu();
                int userChoice = readChoice();

                switch (userChoice){
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
                    scanner.close();
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
            String name = readString("Введите имя: ");
            if (name.isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                return;
            }

            String email = readString("Введите email: ");
            if (email.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            int age = readInteger("Введите возраст: ");

            UserEntity user = userService.createUser(name, email, age);
            System.out.println("Создан: " + user);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.println("\n--- ПОИСК ПО ID ---");

        try {
            int id = readInteger("Введите ID: ");
            UserEntity user = userService.findUserById(id);
            System.out.println("Найден: " + user);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void findUserByEmail() {
        System.out.println("\n--- ПОИСК ПО EMAIL ---");

        try {
            String email = readString("Введите email: ");
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
            if (users.isEmpty()) {
                System.out.println("Пользователей нет.");
            } else {
                System.out.println("Найдено пользователей: " + users.size());
                users.forEach(u -> System.out.println("  " + u));
            }

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateUserById() {
        System.out.println("\n--- ОБНОВЛЕНИЕ ПО ID ---");

        try {
            int id = readInteger("Введите ID пользователя для обновления: ");

            UserEntity existing = userService.findUserById(id);
            System.out.println("Текущие данные: " + existing);

            String name = readString("Введите новое имя: ");
            if (name.isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                return;
            }

            String email = readString("Введите новый email: ");
            if (email.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            int age = readInteger("Введите новый возраст: ");

            UserEntity updated = userService.updateUserById(id, name, email, age);
            System.out.println("Обновлён: " + updated);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateUserByEmail() {
        System.out.println("\n--- ОБНОВЛЕНИЕ ПО EMAIL ---");

        try {
            String currentEmail = readString("Введите текущий email пользователя: ");
            if (currentEmail.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            UserEntity existing = userService.findUserByEmail(currentEmail);
            System.out.println("Текущие данные: " + existing);

            String newName = readString("Введите новое имя: ");
            if (newName.isEmpty()) {
                System.out.println("Имя не может быть пустым!");
                return;
            }

            String newEmail = readString("Введите новый email: ");
            if (newEmail.isEmpty()) {
                System.out.println("Email не может быть пустым!");
                return;
            }

            int newAge = readInteger("Введите новый возраст: ");

            UserEntity updated = userService.updateUserByEmail(currentEmail, newName, newEmail, newAge);
            System.out.println("Обновлён: " + updated);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteUserById() {
        System.out.println("\n--- УДАЛЕНИЕ ПО ID ---");

        try {
            int id = readInteger("Введите ID пользователя для удаления: ");

            UserEntity user = userService.findUserById(id);
            System.out.println("Будет удалён: " + user);

            System.out.print("Подтвердите удаление (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

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




    private static void showMenu(){
        System.out.println("""
                Выберите пункт меню:
                1. Создать пользователя
                2. Найти пользователя по ID
                3. Найти пользователя по Email
                4. Показать всех пользователей
                5. Обновить пользователя по ID
                6. Обновить пользователя по Email
                7. Удалить пользователя по ID
                0. Выйти""");
    }

    private static int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInteger(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число!");
            }
        }
    }
}
