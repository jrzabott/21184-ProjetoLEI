package pt.uab.musicaltrainer.config;

/**
 * Estratégia para SQLite. Caminho configuravel via db.sqlite.path (default: musical-trainer.db).
 */
public class SqliteStrategy implements DatabaseStrategy {
    private final String dbPath;

    public SqliteStrategy(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    public String getUrl() {
        return "jdbc:sqlite:" + dbPath;
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getName() {
        return "SQLite";
    }
}
