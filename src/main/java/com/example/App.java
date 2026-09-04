package com.example;

import org.hibernate.SessionFactory;

import java.util.List;

public class App {
    public static void main(String[] args) {

        System.out.println("\n========================================");
        System.out.println("  ТЕСТИРОВАНИЕ USER SERVICE  ");
        System.out.println("========================================\n");

        try (SessionFactory sessionFactory = HibernateUtil.getSessionFactory()) {
            UserDao userDao = new UserDao(sessionFactory);
            UserService userService = new UserService(userDao);

            // =========================================================
            // 1. ТЕСТ: findAllUsers (пустой список)
            // =========================================================
            System.out.println("\n========== 1. findAllUsers (начальное состояние) ==========");
            List<UserEntity> all = userService.findAllUsers();
            System.out.println("Количество пользователей: " + all.size());

            // =========================================================
            // 2. ТЕСТ: createUser (успешное создание)
            // =========================================================
            System.out.println("\n========== 2. createUser (успех) ==========");
            UserEntity user1 = userService.createUser("Иван Петров", "ivan@test.ru", 30);
            printUser(user1);

            // =========================================================
            // 3. ТЕСТ: createUser (дубликат email — ошибка)
            // =========================================================
            System.out.println("\n========== 3. createUser (дубликат email) ==========");
            UserEntity user2 = userService.createUser("Пётр Иванов", "ivan@test.ru", 25);
            printUser(user2);

            // =========================================================
            // 4. ТЕСТ: createUser (некорректные данные)
            // =========================================================
            System.out.println("\n========== 4. createUser (некорректный email) ==========");
            UserEntity user3 = userService.createUser("Тест", "invalid-email", 20);
            printUser(user3);

            // =========================================================
            // 5. ТЕСТ: createUser (ещё один успешный)
            // =========================================================
            System.out.println("\n========== 5. createUser (второй успешный) ==========");
            UserEntity user4 = userService.createUser("Мария Смирнова", "maria@test.ru", 28);
            printUser(user4);

            // =========================================================
            // 6. ТЕСТ: findUserById (существующий)
            // =========================================================
            System.out.println("\n========== 6. findUserById (существующий) ==========");
            UserEntity foundById = userService.findUserById(1);
            printUser(foundById);

            // =========================================================
            // 7. ТЕСТ: findUserById (несуществующий)
            // =========================================================
            System.out.println("\n========== 7. findUserById (несуществующий) ==========");
            UserEntity notFound = userService.findUserById(9999);
            printUser(notFound);

            // =========================================================
            // 8. ТЕСТ: findUserByEmail (существующий)
            // =========================================================
            System.out.println("\n========== 8. findUserByEmail (существующий) ==========");
            UserEntity foundByEmail = userService.findUserByEmail("ivan@test.ru");
            printUser(foundByEmail);

            // =========================================================
            // 9. ТЕСТ: findUserByEmail (несуществующий)
            // =========================================================
            System.out.println("\n========== 9. findUserByEmail (несуществующий) ==========");
            UserEntity notFoundByEmail = userService.findUserByEmail("notfound@mail.ru");
            printUser(notFoundByEmail);

            // =========================================================
            // 10. ТЕСТ: findAllUsers (после добавления)
            // =========================================================
            System.out.println("\n========== 10. findAllUsers (после добавления) ==========");
            List<UserEntity> allAfter = userService.findAllUsers();
            System.out.println("Количество пользователей: " + allAfter.size());
            allAfter.forEach(u -> System.out.println("  " + u));

            // =========================================================
            // 11. ТЕСТ: updateUserById (успешное обновление)
            // =========================================================
            System.out.println("\n========== 11. updateUserById (успех) ==========");
            UserEntity updated = userService.updateUserById(1, "Иван Сидоров", "ivan_new@test.ru", 35);
            printUser(updated);

            // =========================================================
            // 12. ТЕСТ: updateUserById (несуществующий ID)
            // =========================================================
            System.out.println("\n========== 12. updateUserById (несуществующий ID) ==========");
            UserEntity updatedNotFound = userService.updateUserById(9999, "Тест", "test@mail.ru", 20);
            printUser(updatedNotFound);

            // =========================================================
            // 13. ТЕСТ: updateUserById (email занят другим пользователем)
            // =========================================================
            System.out.println("\n========== 13. updateUserById (email занят) ==========");
            UserEntity conflict = userService.updateUserById(1, "Иван", "maria@test.ru", 35);
            printUser(conflict);

            // =========================================================
            // 14. ТЕСТ: updateUserByEmail (успешное обновление)
            // =========================================================
            System.out.println("\n========== 14. updateUserByEmail (успех) ==========");
            UserEntity updatedByEmail = userService.updateUserByEmail(
                    "ivan_new@test.ru",
                    "Иван Петрович",
                    "ivan_final@test.ru",
                    40
            );
            printUser(updatedByEmail);

            // =========================================================
            // 15. ТЕСТ: updateUserByEmail (несуществующий email)
            // =========================================================
            System.out.println("\n========== 15. updateUserByEmail (несуществующий email) ==========");
            UserEntity updatedNotFoundEmail = userService.updateUserByEmail(
                    "notfound@mail.ru",
                    "Тест",
                    "test@mail.ru",
                    20
            );
            printUser(updatedNotFoundEmail);

            // =========================================================
            // 16. ТЕСТ: deleteUserById (успешное удаление)
            // =========================================================
            System.out.println("\n========== 16. deleteUserById (успех) ==========");
            // Создаём пользователя для удаления
            UserEntity toDelete = userService.createUser("Временный", "temp@test.ru", 25);
            if (toDelete != null) {
                boolean deleted = userService.deleteUserById(toDelete.getId());
                System.out.println("Удаление: " + (deleted ? "✅ успешно" : "❌ не удалось"));
            }

            // =========================================================
            // 17. ТЕСТ: deleteUserById (несуществующий ID)
            // =========================================================
            System.out.println("\n========== 17. deleteUserById (несуществующий ID) ==========");
            boolean deletedNotFound = userService.deleteUserById(9999);
            System.out.println("Удаление несуществующего: " + (deletedNotFound ? "✅" : "❌ ожидаемо не удалось"));

            // =========================================================
            // 18. ТЕСТ: deleteUserById (удаление существующего)
            // =========================================================
            System.out.println("\n========== 18. deleteUserById (удаление существующего) ==========");
            // Находим пользователя по email
            UserEntity toDelete2 = userService.findUserByEmail("maria@test.ru");
            if (toDelete2 != null) {
                boolean deleted2 = userService.deleteUserById(toDelete2.getId());
                System.out.println("Удаление пользователя maria@test.ru: " + (deleted2 ? "✅ успешно" : "❌ не удалось"));
            }

            // =========================================================
            // 19. ТЕСТ: findAllUsers (финальное состояние)
            // =========================================================
            System.out.println("\n========== 19. findAllUsers (финальное состояние) ==========");
            List<UserEntity> finalList = userService.findAllUsers();
            System.out.println("Осталось пользователей: " + finalList.size());
            finalList.forEach(u -> System.out.println("  " + u));

            // =========================================================
            // ЗАВЕРШЕНИЕ
            // =========================================================
            System.out.println("\n========================================");
            System.out.println("ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ");
            System.out.println("========================================");


        } catch (Exception e) {
            System.err.println("Не удалось подключиться к базе данных. Программа завершена.");
            return;
        }
    }

    // ========== ВСПОМОГАТЕЛЬНЫЙ МЕТОД ==========
    private static void printUser(UserEntity user) {
        if (user == null) {
            System.out.println("  ❌ Пользователь не найден / ошибка");
        } else {
            System.out.println("  ✅ " + user);
        }
    }
}
