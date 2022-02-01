package com.weeryan17.mixer.server.data;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

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

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.sqlite.hibernate.dialect.SQLiteDialect");
        properties.setProperty("hibernate.connection.url", url);

        configuration.addProperties(properties);
        sessionFactory = configuration.buildSessionFactory();
    }

}
