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
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public UserEntity update(UserEntity user) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            UserEntity updatedUser = session.merge(user);
            session.getTransaction().commit();
            return updatedUser;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void delete(UserEntity user) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
