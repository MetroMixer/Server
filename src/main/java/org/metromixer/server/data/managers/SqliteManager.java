package org.metromixer.server.data.managers;

import jakarta.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

public class SqliteManager {

    private String file;
    public SqliteManager(String file) {
        this.file = file.endsWith(".db") ? file : file + ".db";
        init();
    }

    private SessionFactory sessionFactory;

    private void init() {
        String url = "jdbc:sqlite:" + this.file;

        Configuration configuration = new Configuration();

        Reflections reflections = new Reflections("org.metromixer.server.data.entities");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class);

        for (Class<?> clas : classes) {
            configuration.addAnnotatedClass(clas);
        }

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.sqlite.hibernate.dialect.SQLiteDialect");
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        configuration.addProperties(properties);
        sessionFactory = configuration.buildSessionFactory();
    }

    public void transaction(Consumer<Session> consumer) {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            try {
                consumer.accept(session);
            } catch (Exception e) {
                session.getTransaction().rollback();
                return;
            }
            session.getTransaction().commit();
        }
    }

}
