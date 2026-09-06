package com.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

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

    public boolean existsByEmail(String email){
        try (Session session = sessionFactory.openSession()) {
            String queryString = """
                    SELECT EXISTS
                    (SELECT 1 FROM UserEntity u
                    WHERE u.email = :email)
                    """;
            return session
                    .createQuery(queryString, Boolean.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }
    }

    public UserEntity save(UserEntity user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public UserEntity update(UserEntity user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            UserEntity updatedUser = session.merge(user);
            transaction.commit();
            return updatedUser;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void delete(UserEntity user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
