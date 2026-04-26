package pt.uab.musicaltrainer.config;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de DataSource usando a estratégia de BD selecionada.
 */
@Configuration
public class DataSourceConfig {

    private final DatabaseFactory databaseFactory;

    public DataSourceConfig(DatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    @Bean
    public DataSource dataSource() throws ClassNotFoundException {
        DatabaseStrategy strategy = databaseFactory.getStrategy();

        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                try {
                    Class.forName(strategy.getDriverClassName());
                    String user = strategy.getUsername().isEmpty() ? null : strategy.getUsername();
                    String pass = strategy.getPassword().isEmpty() ? null : strategy.getPassword();
                    if (user != null) {
                        return java.sql.DriverManager.getConnection(strategy.getUrl(), user, pass);
                    } else {
                        return java.sql.DriverManager.getConnection(strategy.getUrl());
                    }
                } catch (ClassNotFoundException e) {
                    throw new SQLException("driver não encontrado", e);
                }
            }

            @Override
            public Connection getConnection(String user, String password) throws SQLException {
                return getConnection();
            }

            @Override
            public int getLoginTimeout() { return 0; }

            @Override
            public void setLoginTimeout(int seconds) { }

            @Override
            public java.io.PrintWriter getLogWriter() { return null; }

            @Override
            public void setLogWriter(java.io.PrintWriter out) { }

            @Override
            public boolean isWrapperFor(Class<?> iface) { return false; }

            @Override
            public <T> T unwrap(Class<T> iface) { return null; }

            @Override
            public java.util.logging.Logger getParentLogger() { return null; }
        };
    }
}
