package pt.uab.musicaltrainer.config;

/**
 * Estratégia para PostgreSQL (produção alternativa).
 */
public class PostgresStrategy implements DatabaseStrategy {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public PostgresStrategy(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }
}
