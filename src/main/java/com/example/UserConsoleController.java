package com.example;

import java.util.List;

public class UserConsoleController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final ConsoleHelper consoleHelper;

    public UserConsoleController(UserService userService, UserValidator userValidator, ConsoleHelper consoleHelper) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.consoleHelper = consoleHelper;
    }

    public void start() {
        while (true) {
            String menu = """
                    ========== МЕНЮ ==========
                    1.  Создать пользователя
                    2.  Найти по ID
                    3.  Найти по Email
                    4.  Показать всех пользователей
                    5.  Обновить по ID
                    6.  Удалить по ID
                    0.  Выход
                    Для выхода из программы в любой момент времени введите слово "exit" или "выход"
                    """;

            int userChoice = consoleHelper.readInteger(menu);
            switch (userChoice) {
                case 1 -> createUser();
                case 2 -> findUserById();
                case 3 -> findUserByEmail();
                case 4 -> getAllUsers();
                case 5 -> updateUserById();
                case 6 -> deleteUserById();
                case 0 -> {
                    System.out.println("========== До свидания! ==========");
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createUser() {
        System.out.println("\n--- СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ ---");
        try {
            String name = consoleHelper.readValidString("Введите имя: ", userValidator::isNameValid);
            String email = consoleHelper.readValidString("Введите email: ", userValidator::isEmailValid);
            Integer age = consoleHelper.readValidInteger("Введите возраст: ", userValidator::isAgeValid);

            UserEntity createdUser = userService.createUser(name, email, age);
            System.out.println("Пользователь создан: " + createdUser);
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findUserById() {
        System.out.println("\n--- ПОИСК ПОЛЬЗОВАТЕЛЯ ПО ID ---");
        try {
            Integer id = consoleHelper.readValidInteger("Введите id: ", userValidator::isIdValid);
            UserEntity user = userService.findUserById(id);
            System.out.println("Пользователь найден: " + user);
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void findUserByEmail() {
        System.out.println("\n--- ПОИСК ПОЛЬЗОВАТЕЛЯ ПО EMAIL ---");
        try {
            String email = consoleHelper.readValidString("Введите email: ", userValidator::isEmailValid);
            UserEntity user = userService.findUserByEmail(email);
            System.out.println("Пользователь найден: " + user);
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllUsers() {
        System.out.println("\n--- ВСЕ ПОЛЬЗОВАТЕЛИ ---");
        try {
            List<UserEntity> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Пользователей нет");
            } else {
                System.out.printf("Найдено %d пользователей: \n", users.size());
                users.forEach(u -> System.out.println("  " + u));
            }
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateUserById() {
        System.out.println("\n--- ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО ID ---");
        try {
            Integer id = consoleHelper.readValidInteger("Введите id: ", userValidator::isIdValid);
            UserEntity user = userService.findUserById(id);
            System.out.println("Текущие данные пользователя: " + user);

            String name = consoleHelper.readValidString("Введите имя: ", userValidator::isNameValid);
            String email = consoleHelper.readValidString("Введите email: ", userValidator::isEmailValid);
            Integer age = consoleHelper.readValidInteger("Введите возраст: ", userValidator::isAgeValid);

            UserEntity updatedUser = userService.updateUserById(id, name, email, age);
            System.out.println("Пользователь обновлён: " + updatedUser);
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteUserById() {
        System.out.println("\n--- УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ПО ID ---");
        try {
            Integer id = consoleHelper.readValidInteger("Введите id: ", userValidator::isIdValid);
            UserEntity user = userService.findUserById(id);
            System.out.println("Данный пользователь будет удалён: " + user);

            String confirm = consoleHelper.readString("Введите \"yes\" или \"да\" чтобы удалить пользователя: ");
            if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("да")) {
                userService.deleteUserById(id);
                System.out.printf("Пользователь с id=%d удалён\n", id);
            } else {
                System.out.println("Удаление отменено.");
            }

        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }
    }
}
