package com.example;

import org.hibernate.SessionFactory;

import java.util.List;

public class App {

    public static void main(String[] args) {

        System.out.println("\n========================================");
        System.out.println("  ТЕСТИРОВАНИЕ USER SERVICE");
        System.out.println("========================================\n");

        try (SessionFactory sessionFactory = HibernateUtil.getSessionFactory()) {
            UserDao userDao = new UserDao(sessionFactory);
            UserService userService = new UserService(userDao);

            System.out.println("\n========== 1. findAllUsers (начальное состояние) ==========");
            ServiceResult<List<UserEntity>> allResult = userService.findAllUsers();
            if (allResult.isSuccess()) {
                List<UserEntity> all = allResult.getData();
                System.out.println("Количество пользователей: " + all.size());
            } else {
                System.out.println(allResult.getErrorMessage());
            }

            System.out.println("\n========== 2. createUser (успех) ==========");
            ServiceResult<UserEntity> result1 = userService.createUser("Иван Петров", "ivan@test.ru", 30);
            printResult(result1);

            System.out.println("\n========== 3. createUser (дубликат email) ==========");
            ServiceResult<UserEntity> result2 = userService.createUser("Пётр Иванов", "ivan@test.ru", 25);
            printResult(result2);

            System.out.println("\n========== 4. createUser (некорректный email) ==========");
            ServiceResult<UserEntity> result3 = userService.createUser("Тест", "invalid-email", 20);
            printResult(result3);

            System.out.println("\n========== 5. createUser (второй успешный) ==========");
            ServiceResult<UserEntity> result4 = userService.createUser("Мария Смирнова", "maria@test.ru", 28);
            printResult(result4);

            System.out.println("\n========== 6. findUserById (существующий) ==========");
            ServiceResult<UserEntity> result5 = userService.findUserById(1);
            printResult(result5);

            System.out.println("\n========== 7. findUserById (несуществующий) ==========");
            ServiceResult<UserEntity> result6 = userService.findUserById(9999);
            printResult(result6);

            System.out.println("\n========== 8. findUserByEmail (существующий) ==========");
            ServiceResult<UserEntity> result7 = userService.findUserByEmail("ivan@test.ru");
            printResult(result7);

            System.out.println("\n========== 9. findUserByEmail (несуществующий) ==========");
            ServiceResult<UserEntity> result8 = userService.findUserByEmail("notfound@mail.ru");
            printResult(result8);

            System.out.println("\n========== 10. findAllUsers (после добавления) ==========");
            ServiceResult<List<UserEntity>> allAfterResult = userService.findAllUsers();
            if (allAfterResult.isSuccess()) {
                List<UserEntity> allAfter = allAfterResult.getData();
                System.out.println("Количество пользователей: " + allAfter.size());
                allAfter.forEach(u -> System.out.println("  " + u));
            } else {
                System.out.println(allAfterResult.getErrorMessage());
            }

            System.out.println("\n========== 11. updateUserById (успех) ==========");
            ServiceResult<UserEntity> result9 = userService.updateUserById(1, "Иван Сидоров", "ivan_new@test.ru", 35);
            printResult(result9);

            System.out.println("\n========== 12. updateUserById (несуществующий ID) ==========");
            ServiceResult<UserEntity> result10 = userService.updateUserById(9999, "Тест", "test@mail.ru", 20);
            printResult(result10);

            System.out.println("\n========== 13. updateUserById (email занят) ==========");
            ServiceResult<UserEntity> result11 = userService.updateUserById(1, "Иван", "maria@test.ru", 35);
            printResult(result11);

            System.out.println("\n========== 14. updateUserByEmail (успех) ==========");
            ServiceResult<UserEntity> result12 = userService.updateUserByEmail(
                    "ivan_new@test.ru",
                    "Иван Петрович",
                    "ivan_final@test.ru",
                    40
            );
            printResult(result12);

            System.out.println("\n========== 15. updateUserByEmail (несуществующий email) ==========");
            ServiceResult<UserEntity> result13 = userService.updateUserByEmail(
                    "notfound@mail.ru",
                    "Тест",
                    "test@mail.ru",
                    20
            );
            printResult(result13);

            System.out.println("\n========== 16. deleteUserById (успех) ==========");
            ServiceResult<UserEntity> toDeleteResult = userService.createUser("Временный", "temp@test.ru", 25);
            if (toDeleteResult.isSuccess()) {
                UserEntity toDelete = toDeleteResult.getData();
                ServiceResult<Boolean> deleteResult = userService.deleteUserById(toDelete.getId());
                System.out.println("Удаление: " + (deleteResult.isSuccess() ? "успешно" : deleteResult.getErrorMessage()));
            } else {
                System.out.println("Не удалось создать пользователя для удаления");
            }

            System.out.println("\n========== 17. deleteUserById (несуществующий ID) ==========");
            ServiceResult<Boolean> deleteResult2 = userService.deleteUserById(9999);
            System.out.println("Удаление несуществующего: " + (deleteResult2.isSuccess()
                    ? "успешно (неожиданно)"
                    : "ожидаемо не удалось: " + deleteResult2.getErrorMessage()));

            System.out.println("\n========== 18. deleteUserById (удаление существующего) ==========");
            ServiceResult<UserEntity> findForDelete = userService.findUserByEmail("maria@test.ru");
            if (findForDelete.isSuccess()) {
                UserEntity toDelete2 = findForDelete.getData();
                ServiceResult<Boolean> deleteResult3 = userService.deleteUserById(toDelete2.getId());
                System.out.println("Удаление пользователя maria@test.ru: "
                        + (deleteResult3.isSuccess() ? "успешно" : deleteResult3.getErrorMessage()));
            } else {
                System.out.println("Пользователь с email maria@test.ru не найден для удаления");
            }

            System.out.println("\n========== 19. findAllUsers (финальное состояние) ==========");
            ServiceResult<List<UserEntity>> finalResult = userService.findAllUsers();
            if (finalResult.isSuccess()) {
                List<UserEntity> finalList = finalResult.getData();
                System.out.println("Осталось пользователей: " + finalList.size());
                finalList.forEach(u -> System.out.println("  " + u));
            } else {
                System.out.println(finalResult.getErrorMessage());
            }

            System.out.println("\n========================================");
            System.out.println("ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("Не удалось подключиться к базе данных. Программа завершена.");
            e.printStackTrace();
        }
    }

    private static void printResult(ServiceResult<UserEntity> result) {
        if (result.isSuccess()) {
            System.out.println("Ответ программы: " + result.getData());
        } else {
            System.out.println("Ошибка для пользователя: " + result.getErrorMessage());
        }
    }
}
