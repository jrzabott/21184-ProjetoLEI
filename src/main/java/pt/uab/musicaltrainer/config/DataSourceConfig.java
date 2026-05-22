package pt.uab.musicaltrainer.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
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
        // atenção: SQLite rejeitava AUTO_INCREMENT do schema.sql (erro de sintaxe H2-specific)
        // schema-sqlite.sql usa INTEGER PRIMARY KEY que é alias do rowid e auto-incrementa
        if (strategy instanceof H2Strategy) {
            initSchema(ds, "schema.sql");
        } else if (strategy instanceof SqliteStrategy) {
            initSchema(ds, "schema-sqlite.sql");
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
            @Override public PrintWriter getLogWriter() { return null; }
            @Override public void setLogWriter(PrintWriter o) { }
            @Override public boolean isWrapperFor(Class<?> i) { return false; }
            @Override public <T> T unwrap(Class<T> i) { return null; }
            @Override public java.util.logging.Logger getParentLogger() { return null; }
        };
    }

    private void initSchema(DataSource ds, String schemaFileName) {
        try {
            ClassPathResource schemaFile = new ClassPathResource(schemaFileName);
            if (!schemaFile.exists()) {
                logger.warn("{} não encontrado no classpath - BD pode estar vazia", schemaFileName);
                return;
            }

            String sql;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(schemaFile.getInputStream(), StandardCharsets.UTF_8))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }

            try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
                // executar cada statement separadamente (H2 e SQLite nao aceitam multiplos por execute())
                for (String statement : sql.split(";")) {
                    // remover apenas linhas de comentario, nao o statement inteiro
                    String cleaned = Arrays.stream(statement.split("\n"))
                        .filter(line -> !line.trim().startsWith("--"))
                        .collect(Collectors.joining("\n"))
                        .trim();
                    if (!cleaned.isEmpty()) {
                        stmt.execute(cleaned);
                    }
                }
                logger.info("Schema inicializado com sucesso a partir de {}", schemaFileName);
            }
        } catch (Exception e) {
            logger.error("Erro ao inicializar schema ({}): {}", schemaFileName, e.getMessage(), e);
            throw new RuntimeException("Falha na inicialização do schema da BD", e);
        }
    }
}
