package pt.uab.musicaltrainer.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Configuração de DataSource usando a estratégia de BD selecionada.
 * Inicializa o schema manualmente para garantir compatibilidade com H2 in-memory.
 */
@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    private final DatabaseFactory databaseFactory;

    public DataSourceConfig(DatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        DatabaseStrategy strategy = databaseFactory.getStrategy();
        logger.info("Criando DataSource: strategy={}, url={}", strategy.getName(), strategy.getUrl());

        Class.forName(strategy.getDriverClassName());

        // Usar anonymous DataSource que mantém a ligação via DriverManager
        DataSource ds = buildDataSource(strategy);

        // H2 in-memory perde o estado entre reinícios - inicializar schema explicitamente.
        // SQLite e Postgres gerem o seu próprio schema (criado na primeira instalação).
        if (strategy instanceof H2Strategy) {
            initSchema(ds);
        }

        logger.info("DataSource pronto: strategy={}", strategy.getName());
        return ds;
    }

    private DataSource buildDataSource(DatabaseStrategy strategy) {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                try {
                    String user = strategy.getUsername().isEmpty() ? null : strategy.getUsername();
                    String pass = strategy.getPassword().isEmpty() ? null : strategy.getPassword();
                    if (user != null) {
                        return java.sql.DriverManager.getConnection(strategy.getUrl(), user, pass);
                    }
                    return java.sql.DriverManager.getConnection(strategy.getUrl());
                } catch (Exception e) {
                    throw new SQLException("Falha ao obter ligação: " + e.getMessage(), e);
                }
            }

            @Override
            public Connection getConnection(String u, String p) throws SQLException {
                return getConnection();
            }

            @Override public int getLoginTimeout() { return 0; }
            @Override public void setLoginTimeout(int s) { }
            @Override public java.io.PrintWriter getLogWriter() { return null; }
            @Override public void setLogWriter(java.io.PrintWriter o) { }
            @Override public boolean isWrapperFor(Class<?> i) { return false; }
            @Override public <T> T unwrap(Class<T> i) { return null; }
            @Override public java.util.logging.Logger getParentLogger() { return null; }
        };
    }

    private void initSchema(DataSource ds) {
        try {
            ClassPathResource schemaFile = new ClassPathResource("schema.sql");
            if (!schemaFile.exists()) {
                logger.warn("schema.sql não encontrado no classpath - BD pode estar vazia");
                return;
            }

            String sql;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(schemaFile.getInputStream(), StandardCharsets.UTF_8))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }

            try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
                // Executar cada statement separadamente (H2 não aceita múltiplos por execute())
                for (String statement : sql.split(";")) {
                    // Remover apenas linhas de comentário, não o statement inteiro
                    String cleaned = java.util.Arrays.stream(statement.split("\n"))
                        .filter(line -> !line.trim().startsWith("--"))
                        .collect(Collectors.joining("\n"))
                        .trim();
                    if (!cleaned.isEmpty()) {
                        stmt.execute(cleaned);
                    }
                }
                logger.info("Schema inicializado com sucesso a partir de schema.sql");
            }
        } catch (Exception e) {
            logger.error("Erro ao inicializar schema: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na inicialização do schema da BD", e);
        }
    }
}
