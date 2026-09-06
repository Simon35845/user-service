package com.example.console;

import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Класс для обработки консольного ввода с проверкой ввода на корректность.
 * Имеется возможность остановки работы приложения при помощи специальных слов.
 *
 * @author FlameFlow2001 (Shundev Kirill): методы checkShutdownCommand, readString, readInteger
 *
 * @author Simon35845: методы readValidString, readValidInteger
 */
public class ConsoleHelper {

    private final Scanner scanner;

    public ConsoleHelper() {
        this.scanner = new Scanner(System.in);
    }

    public void checkShutdownCommand(String string) {
        if (string != null
                && (string.equalsIgnoreCase("exit")
                || string.equalsIgnoreCase("выход"))
        ) {
            System.out.println("========== До свидания! ==========");
            System.exit(0);
        }
    }

    public String readString(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        checkShutdownCommand(input);
        return input;
    }

    public Integer readInteger(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            checkShutdownCommand(input);

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
            }
        }
    }

    public String readValidString(String message, Predicate<String> predicate) {
        String input;
        do {
            System.out.print(message);
            input = scanner.nextLine().trim();
            checkShutdownCommand(input);
        } while (!predicate.test(input));
        return input;
    }

    public Integer readValidInteger(String message, Predicate<Integer> predicate) {
        Integer number = null;
        do {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            checkShutdownCommand(input);

            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
            }
        } while (!predicate.test(number));
        return number;
    }
}
