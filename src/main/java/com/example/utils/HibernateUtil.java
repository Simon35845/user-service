package com.example.utils;

import com.example.entity.UserEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Класс для настройки Hibernate SessionFactory. Использует настройки из файла hibernate.cfg.xml.
 *
 * @author Simon35845
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(UserEntity.class);

            sessionFactory = configuration.buildSessionFactory();
        }
        return sessionFactory;
    }
}
