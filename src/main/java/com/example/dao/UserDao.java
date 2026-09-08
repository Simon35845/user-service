package com.example.dao;

import com.example.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Этот класс предназначен для выполнения CRUD операций в БД с применением Hibernate.
 *
 * @author Simon35845
 */
public class UserDao {

    private final SessionFactory sessionFactory;

    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public UserEntity findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(UserEntity.class, id);
        }
    }

    public List<UserEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            String queryString = """
                    SELECT u FROM UserEntity u
                    ORDER BY u.id
                    """;
            return session
                    .createQuery(queryString, UserEntity.class)
                    .list();
        }
    }

    public UserEntity findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = """
                    SELECT u FROM UserEntity u
                    WHERE u.email = :email
                    """;
            return session
                    .createQuery(queryString, UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }
    }

    public UserEntity save(UserEntity user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                return user;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public UserEntity update(UserEntity user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                UserEntity updatedUser = session.merge(user);
                transaction.commit();
                return updatedUser;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public void delete(UserEntity user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }
}
