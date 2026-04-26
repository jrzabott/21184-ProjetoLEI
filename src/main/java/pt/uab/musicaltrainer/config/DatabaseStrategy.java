package pt.uab.musicaltrainer.config;

/**
 * Estratégia para configuração de base de dados.
 * Define o contrato para diferentes implementações de BD (H2, SQLite, PostgreSQL).
 */
public interface DatabaseStrategy {
    /**
     * Retorna o driver JDBC para esta base de dados.
     */
    String getDriverClassName();

    /**
     * Retorna a URL de conexão JDBC.
     */
    String getUrl();

    /**
     * Retorna o utilizador de base de dados (se aplicável).
     */
    String getUsername();

    /**
     * Retorna a senha (se aplicável).
     */
    String getPassword();

    /**
     * Retorna o nome da estratégia para logging/debug.
     */
    String getName();
}
