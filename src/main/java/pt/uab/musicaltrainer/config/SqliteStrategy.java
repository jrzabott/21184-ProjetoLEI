package pt.uab.musicaltrainer.config;

/**
 * Estratégia para SQLite (produção por defeito).
 */
public class SqliteStrategy implements DatabaseStrategy {
    private static final String DB_PATH = "musical-trainer.db";

    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    public String getUrl() {
        return "jdbc:sqlite:" + DB_PATH;
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
