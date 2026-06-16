package pt.uab.musicaltrainer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Factory para selecionar estratégia de BD baseada em propriedades.
 * Usa Strategy pattern para abstrair configurações de BD (H2, SQLite, PostgreSQL).
 */
@Component
public class DatabaseFactory {
    private final String databaseType;
    private final String postgresHost;
    private final int postgresPort;
    private final String postgresDatabase;
    private final String postgresUsername;
    private final String postgresPassword;
    private final String sqlitePath;

    public DatabaseFactory(
            @Value("${db.type:H2}") String databaseType,
            @Value("${db.postgres.host:localhost}") String postgresHost,
            @Value("${db.postgres.port:5432}") int postgresPort,
            @Value("${db.postgres.database:musical_trainer}") String postgresDatabase,
            @Value("${db.postgres.username:postgres}") String postgresUsername,
            @Value("${db.postgres.password:}") String postgresPassword,
            @Value("${db.sqlite.path:musical-trainer.db}") String sqlitePath) {
        this.databaseType = databaseType;
        this.postgresHost = postgresHost;
        this.postgresPort = postgresPort;
        this.postgresDatabase = postgresDatabase;
        this.postgresUsername = postgresUsername;
        this.postgresPassword = postgresPassword;
        this.sqlitePath = sqlitePath;
    }

    /**
     * Retorna a estratégia de BD apropriada baseada na propriedade db.type.
     */
    public DatabaseStrategy getStrategy() {
        return switch (databaseType.toUpperCase()) {
            case "SQLITE" -> new SqliteStrategy(sqlitePath);
            case "POSTGRES" -> new PostgresStrategy(postgresHost, postgresPort, postgresDatabase, postgresUsername, postgresPassword);
            case "H2" -> new H2Strategy();
            default -> throw new IllegalArgumentException("Tipo de BD desconhecido: " + databaseType);
        };
    }

    /**
     * Retorna o nome da BD atualmente configurada.
     */
    public String getDatabaseName() {
        return getStrategy().getName();
    }
}
