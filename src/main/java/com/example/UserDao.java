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

    public void save(UserEntity user) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(user);
            transaction.commit();
        }
    }

    public UserEntity update(UserEntity user) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            UserEntity updatedUser = session.merge(user);
            transaction.commit();
            return updatedUser;
        }
    }
//тут логика сервисная
    public UserEntity updateById(Integer id, UserEntity user) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            UserEntity userToUpdate = session.find(UserEntity.class, id);
            userToUpdate.setName(user.getName());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setAge(user.getAge());
            transaction.commit();
            return userToUpdate;
        }
    }

    public void delete(UserEntity user) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            session.remove(user);
            transaction.commit();
        }
    }
//логика сервисная
    public void deleteById(Integer id) {
        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
            UserEntity userToDelete = session.find(UserEntity.class, id);
            session.remove(userToDelete);
            transaction.commit();
        }
    }
}
