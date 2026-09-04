package com.example;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class App {

    public static void main(String[] args) {
        System.out.println("\n========================================");
        System.out.println("  ТЕСТИРОВАНИЕ USER SERVICE");
        System.out.println("========================================\n");

        // ========== ИНИЦИАЛИЗАЦИЯ ==========
        try (SessionFactory sessionFactory = HibernateUtil.getSessionFactory()) {
            UserDao userDao = new UserDao(sessionFactory);
            UserService userService = new UserService(userDao);

            // =========================================================
            // 1. findAllUsers (начальное состояние)
            // =========================================================
            System.out.println("\n========== 1. findAllUsers (начальное состояние) ==========");
            try {
                List<UserEntity> users = userService.findAllUsers();
                System.out.println("✅ Найдено пользователей: " + users.size());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 2. createUser (успешное создание)
            // =========================================================
            System.out.println("\n========== 2. createUser (успех) ==========");
            try {
                UserEntity user = userService.createUser("Иван Петров", "ivan@test.ru", 30);
                System.out.println("✅ Создан: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 3. createUser (дубликат email — ошибка)
            // =========================================================
            System.out.println("\n========== 3. createUser (дубликат email) ==========");
            try {
                UserEntity user = userService.createUser("Пётр Иванов", "ivan@test.ru", 25);
                System.out.println("✅ Создан: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage()); // Ожидаем: "Email уже существует"
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 4. createUser (некорректные данные)
            // =========================================================
            System.out.println("\n========== 4. createUser (некорректный email) ==========");
            try {
                UserEntity user = userService.createUser("Тест", "invalid-email", 20);
                System.out.println("✅ Создан: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 5. createUser (ещё один успешный)
            // =========================================================
            System.out.println("\n========== 5. createUser (второй успешный) ==========");
            try {
                UserEntity user = userService.createUser("Мария Смирнова", "maria@test.ru", 28);
                System.out.println("✅ Создан: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 6. findUserById (существующий)
            // =========================================================
            System.out.println("\n========== 6. findUserById (существующий) ==========");
            try {
                UserEntity user = userService.findUserById(1);
                System.out.println("✅ Найден: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 7. findUserById (несуществующий)
            // =========================================================
            System.out.println("\n========== 7. findUserById (несуществующий) ==========");
            try {
                UserEntity user = userService.findUserById(9999);
                System.out.println("✅ Найден: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage()); // Ожидаем: "Пользователь не найден"
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 8. findUserByEmail (существующий)
            // =========================================================
            System.out.println("\n========== 8. findUserByEmail (существующий) ==========");
            try {
                UserEntity user = userService.findUserByEmail("ivan@test.ru");
                System.out.println("✅ Найден: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 9. findUserByEmail (несуществующий)
            // =========================================================
            System.out.println("\n========== 9. findUserByEmail (несуществующий) ==========");
            try {
                UserEntity user = userService.findUserByEmail("notfound@mail.ru");
                System.out.println("✅ Найден: " + user);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 10. findAllUsers (после добавления)
            // =========================================================
            System.out.println("\n========== 10. findAllUsers (после добавления) ==========");
            try {
                List<UserEntity> users = userService.findAllUsers();
                System.out.println("✅ Найдено пользователей: " + users.size());
                users.forEach(u -> System.out.println("  " + u));
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 11. updateUserById (успешное обновление)
            // =========================================================
            System.out.println("\n========== 11. updateUserById (успех) ==========");
            try {
                UserEntity updated = userService.updateUserById(1, "Иван Сидоров", "ivan_new@test.ru", 35);
                System.out.println("✅ Обновлён: " + updated);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 12. updateUserById (несуществующий ID)
            // =========================================================
            System.out.println("\n========== 12. updateUserById (несуществующий ID) ==========");
            try {
                UserEntity updated = userService.updateUserById(9999, "Тест", "test@mail.ru", 20);
                System.out.println("✅ Обновлён: " + updated);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 13. updateUserById (email занят другим пользователем)
            // =========================================================
            System.out.println("\n========== 13. updateUserById (email занят) ==========");
            try {
                UserEntity updated = userService.updateUserById(1, "Иван", "maria@test.ru", 35);
                System.out.println("✅ Обновлён: " + updated);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 14. updateUserByEmail (успешное обновление)
            // =========================================================
            System.out.println("\n========== 14. updateUserByEmail (успех) ==========");
            try {
                UserEntity updated = userService.updateUserByEmail(
                        "ivan_new@test.ru",
                        "Иван Петрович",
                        "ivan_final@test.ru",
                        40
                );
                System.out.println("✅ Обновлён: " + updated);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 15. updateUserByEmail (несуществующий email)
            // =========================================================
            System.out.println("\n========== 15. updateUserByEmail (несуществующий email) ==========");
            try {
                UserEntity updated = userService.updateUserByEmail(
                        "notfound@mail.ru",
                        "Тест",
                        "test@mail.ru",
                        20
                );
                System.out.println("✅ Обновлён: " + updated);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 16. deleteUserById (успешное удаление)
            // =========================================================
            System.out.println("\n========== 16. deleteUserById (успех) ==========");
            try {
                // Создаём пользователя для удаления
                UserEntity toDelete = userService.createUser("Временный", "temp@test.ru", 25);
                System.out.println("✅ Создан для удаления: " + toDelete);

                userService.deleteUserById(toDelete.getId());
                System.out.println("✅ Пользователь удалён");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 17. deleteUserById (несуществующий ID)
            // =========================================================
            System.out.println("\n========== 17. deleteUserById (несуществующий ID) ==========");
            try {
                userService.deleteUserById(9999);
                System.out.println("✅ Пользователь удалён");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 18. deleteUserById (удаление существующего)
            // =========================================================
            System.out.println("\n========== 18. deleteUserById (удаление существующего) ==========");
            try {
                UserEntity toDelete = userService.findUserByEmail("maria@test.ru");
                if (toDelete != null) {
                    userService.deleteUserById(toDelete.getId());
                    System.out.println("✅ Пользователь maria@test.ru удалён");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // 19. findAllUsers (финальное состояние)
            // =========================================================
            System.out.println("\n========== 19. findAllUsers (финальное состояние) ==========");
            try {
                List<UserEntity> users = userService.findAllUsers();
                System.out.println("✅ Осталось пользователей: " + users.size());
                users.forEach(u -> System.out.println("  " + u));
            } catch (RuntimeException e) {
                System.out.println("❌ " + e.getMessage());
            }

            // =========================================================
            // ЗАВЕРШЕНИЕ
            // =========================================================
            System.out.println("\n========================================");
            System.out.println("ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("❌ Не удалось подключиться к базе данных. Программа завершена.");
            e.printStackTrace();
        }
    }
}
