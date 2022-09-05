package org.metromixer.server.data.managers;

import jakarta.persistence.Entity;
import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.reflections.Reflections;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

public class DerbyManager {

    private final String file;
    public DerbyManager(String file) {
        this.file = file;
        init();
    }

    private SessionFactory sessionFactory;

    private void init() {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setCreateDatabase("create");
        dataSource.setDatabaseName(file);

        try {
            Connection connection = dataSource.getConnection();
            if (!connection.isValid(1000)) {
                connection.close();
                throw new SQLException("Couldn't connect to database");
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }

        DatasourceConnectionProviderImpl connectionProvider = new DatasourceConnectionProviderImpl();
        connectionProvider.setDataSource(dataSource);

        FluentConfiguration flywayConfig = Flyway.configure(getClass().getClassLoader())
                .baselineVersion("0")
                .validateMigrationNaming(true)
                .baselineOnMigrate(true)
                .locations("db")
                .dataSource(dataSource);

        Flyway flyway = flywayConfig.load();
        flyway.migrate();

        Configuration configuration = new Configuration();

        Reflections reflections = new Reflections("org.metromixer.server.data.entities");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class);

        for (Class<?> clas : classes) {
            configuration.addAnnotatedClass(clas);
        }

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.setProperty("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver");

        configuration.addProperties(properties);
        sessionFactory = configuration.buildSessionFactory(new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).applySetting(Environment.CONNECTION_PROVIDER, connectionProvider).build());
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
