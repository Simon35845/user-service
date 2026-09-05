package com.example;

import java.util.List;
import java.util.Scanner;

public class ConsoleView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        System.out.println("""
            Выберите пункт меню
            ========== МЕНЮ ==========
            1.  Создать пользователя
            2.  Найти по ID
            3.  Найти по Email
            4.  Показать всех пользователей
            5.  Обновить по ID
            6.  Обновить по Email
            7.  Удалить по ID
            0.  Выход""");
    }

    public int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("выход")) {
            System.out.println("Выход из программы, т.к. было введено exit или выход");
            System.exit(0);
        }

        return input;
    }

    public Integer readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("выход")) {
                System.out.println("Выход из программы, т.к. было введено exit или выход");
                System.exit(0);
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
            }
        }
    }

    public void printUsers(List<UserEntity> users) {
        if (users.isEmpty()) {
            System.out.println("Пользователей нет.");
        } else {
            System.out.println("Найдено пользователей: " + users.size());
            users.forEach(u -> System.out.println("  " + u));
        }
    }

    public void closeScanner() {
        scanner.close();
    }

}