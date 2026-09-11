package com.example.dao;

import com.example.entity.UserEntity;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoTest {

    @Container
    private static final PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private static UserDao userDao;

    /**
     * В этом методе настраивается SessionFactory. Перед запуском всех тестов заново создаётся таблица users.
     * Затем создаются sessionFactory и userDao. После происходит заполнение таблицы users записями.
     *
     * @author Simon35845
     */
    @BeforeAll
    static void init() {
        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", POSTGRES_CONTAINER.getJdbcUrl())
                .setProperty("hibernate.connection.username", POSTGRES_CONTAINER.getUsername())
                .setProperty("hibernate.connection.password", POSTGRES_CONTAINER.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .addAnnotatedClass(UserEntity.class);

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDao(sessionFactory);
        fillDatabase();
    }

    /**
     * @author Simon35845
     */
    @AfterAll
    static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * @author Simon35845
     */
    @Test
    void findById_ifUserExists() {
        Integer id = 3;
        UserEntity expectedUser = new UserEntity("Charlie", "charlie@example.com", 33);
        UserEntity actualUser = userDao.findById(id);

        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getAge(), actualUser.getAge());
    }

    /**
     * @author Simon35845
     */
    @Test
    void findById_ifUserNotExists() {
        Integer id = 9;
        UserEntity actualUser = userDao.findById(id);
        assertNull(actualUser);
    }

    /**
     * @author Simon35845
     */
    @Test
    void findAll() {
        List<UserEntity> users = userDao.findAll();
        assertEquals(3, users.size());
    }

    /**
     * @author Yushinova
     */
    @Test
    void findByEmail_isUserExists() {
        String email = "alice@example.com";
        UserEntity expectedUser = new UserEntity("Alice", "alice@example.com", 24);
        UserEntity actualUser = userDao.findByEmail(email);
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getAge(), actualUser.getAge());
    }

    /**
     * @author Yushinova
     */
    @Test
    void findByEmail_ifUserNotExists() {
        String email = "notfound@test.ru";
        assertThrows(
                NoResultException.class,
                () -> userDao.findByEmail(email)
        );
    }

    /**
     * @author Yushinova
     */
    @Test
    void delete_ifUserExists() {
        Integer id = 3;
        UserEntity deletedUser = userDao.findById(id);
        assertNotNull(deletedUser);
        userDao.delete(deletedUser);
        UserEntity afterDelete = userDao.findById(id);
        assertNull(afterDelete);
    }

    /**
     * @author Yushinova
     */
    @Test
    void delete_ifUserNotExists() {
        UserEntity deletedUser = new UserEntity("Alice", "notfounde@example.com", 24);
        deletedUser.setId(99);
        assertThrows(
                OptimisticLockException.class,
                () -> userDao.delete(deletedUser)
        );
    }

    /**
     * @author Simon35845
     */
    private static void fillDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            UserEntity user1 = new UserEntity("Alice", "alice@example.com", 24);
            UserEntity user2 = new UserEntity("Bob", "bob@example.com", 31);
            UserEntity user3 = new UserEntity("Charlie", "charlie@example.com", 33);
            session.persist(user1);
            session.persist(user2);
            session.persist(user3);
            session.getTransaction().commit();
        }
    }
}