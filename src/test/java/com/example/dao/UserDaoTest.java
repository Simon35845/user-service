//package com.example.dao;
//
//import com.example.entity.UserEntity;
//import jakarta.persistence.NoResultException;
//import jakarta.persistence.OptimisticLockException;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Testcontainers
//class UserDaoTest {
//
//    @Container
//    private static final PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:16")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test");
//
//    private static SessionFactory sessionFactory;
//    private static UserDao userDao;
//
//    /**
//     * В этом методе настраивается SessionFactory. Перед запуском всех тестов заново создаётся таблица users.
//     * Затем создаются sessionFactory и userDao. После происходит заполнение таблицы users записями.
//     *
//     * @author Simon35845
//     */
//    @BeforeAll
//    static void init() {
//        Configuration configuration = new Configuration()
//                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
//                .setProperty("hibernate.connection.url", POSTGRES_CONTAINER.getJdbcUrl())
//                .setProperty("hibernate.connection.username", POSTGRES_CONTAINER.getUsername())
//                .setProperty("hibernate.connection.password", POSTGRES_CONTAINER.getPassword())
//                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
//                .setProperty("hibernate.show_sql", "true")
//                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
//                .addAnnotatedClass(UserEntity.class);
//
//        sessionFactory = configuration.buildSessionFactory();
//        userDao = new UserDao(sessionFactory);
//        fillDatabase();
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @AfterAll
//    static void shutdown() {
//        if (sessionFactory != null) {
//            sessionFactory.close();
//        }
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void createUser_ifValid(){
//        UserEntity newUser = new UserEntity("Dave", "dave@example.com", 40);
//        assertNull(newUser.getId(), "До сохранения id должен быть null");
//
//        UserEntity savedUser = userDao.save(newUser);
//
//        assertNotNull(savedUser.getId(), "После сохранения id должен быть присвоен");
//        assertEquals("Dave", savedUser.getName());
//        assertEquals("dave@example.com", savedUser.getEmail());
//        assertEquals(40, savedUser.getAge());
//
//        UserEntity fromDb = userDao.findById(savedUser.getId());
//        assertNotNull(fromDb);
//        assertEquals("Dave", fromDb.getName());
//        assertEquals("dave@example.com", fromDb.getEmail());
//        assertEquals(40, fromDb.getAge());
//        assertNotNull(fromDb.getCreatedAt(), "createdAt должен проставиться через @PrePersist");
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void createUser_ifEmailDuplicate() {
//        UserEntity duplicate = new UserEntity("Fake Alice", "alice@example.com", 99);
//
//        assertThrows(
//                org.hibernate.exception.ConstraintViolationException.class,
//                () -> userDao.save(duplicate)
//        );
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void updateUserById_ifUserExists() {
//        UserEntity existing = userDao.findById(1);
//        assertNotNull(existing);
//
//        existing.setName("Alice Updated");
//        existing.setEmail("alice.updated@example.com");
//        existing.setAge(25);
//
//        UserEntity updated = userDao.update(existing);
//        assertNotNull(updated);
//        assertEquals(1, updated.getId());
//
//        UserEntity fromDb = userDao.findById(1);
//        assertNotNull(fromDb);
//        assertEquals("Alice Updated", fromDb.getName());
//        assertEquals("alice.updated@example.com", fromDb.getEmail());
//        assertEquals(25, fromDb.getAge());
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void updateUserById_ifUserNotExists() {
//        UserEntity ghost = new UserEntity("Ghost", "ghost@example.com", 30);
//        ghost.setId(999);
//
//        // Убеждаемся, что такого пользователя нет
//        assertNull(userDao.findById(999));
//
//        assertThrows(
//                jakarta.persistence.OptimisticLockException.class,
//                () -> userDao.update(ghost)
//        );
//
//        assertNull(userDao.findById(999));
//    }
//
//    /**
//     * @author FlameFlow21 (Shundev Kirill)
//     */
//    @Test
//    void updateUserById_ifEmailDuplicate() {
//        UserEntity bob = userDao.findById(2);
//        assertNotNull(bob);
//        bob.setEmail("charlie@example.com");
//
//        assertThrows(
//                org.hibernate.exception.ConstraintViolationException.class,
//                () -> userDao.update(bob)
//        );
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void findById_ifUserExists() {
//        Integer id = 3;
//        UserEntity expectedUser = new UserEntity("Charlie", "charlie@example.com", 33);
//        UserEntity actualUser = userDao.findById(id);
//
//        assertEquals(expectedUser.getName(), actualUser.getName());
//        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
//        assertEquals(expectedUser.getAge(), actualUser.getAge());
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void findById_ifUserNotExists() {
//        Integer id = 9;
//        UserEntity actualUser = userDao.findById(id);
//        assertNull(actualUser);
//    }
//
//    /**
//     * @author Simon35845
//     */
//    @Test
//    void findAll() {
//        List<UserEntity> users = userDao.findAll();
//        assertEquals(3, users.size());
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void findByEmail_isUserExists() {
//        String email = "alice@example.com";
//        UserEntity expectedUser = new UserEntity("Alice", "alice@example.com", 24);
//        UserEntity actualUser = userDao.findByEmail(email);
//        assertEquals(expectedUser.getName(), actualUser.getName());
//        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
//        assertEquals(expectedUser.getAge(), actualUser.getAge());
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void findByEmail_ifUserNotExists() {
//        String email = "notfound@test.ru";
//        assertThrows(
//                NoResultException.class,
//                () -> userDao.findByEmail(email)
//        );
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void delete_ifUserExists() {
//        Integer id = 3;
//        UserEntity deletedUser = userDao.findById(id);
//        assertNotNull(deletedUser);
//        userDao.delete(deletedUser);
//        UserEntity afterDelete = userDao.findById(id);
//        assertNull(afterDelete);
//    }
//
//    /**
//     * @author Yushinova
//     */
//    @Test
//    void delete_ifUserNotExists() {
//        UserEntity deletedUser = new UserEntity("Alice", "notfounde@example.com", 24);
//        deletedUser.setId(99);
//        assertThrows(
//                OptimisticLockException.class,
//                () -> userDao.delete(deletedUser)
//        );
//    }
//
//    /**
//     * @author Simon35845
//     */
//    private static void fillDatabase() {
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            UserEntity user1 = new UserEntity("Alice", "alice@example.com", 24);
//            UserEntity user2 = new UserEntity("Bob", "bob@example.com", 31);
//            UserEntity user3 = new UserEntity("Charlie", "charlie@example.com", 33);
//            session.persist(user1);
//            session.persist(user2);
//            session.persist(user3);
//            session.getTransaction().commit();
//        }
//    }
//}