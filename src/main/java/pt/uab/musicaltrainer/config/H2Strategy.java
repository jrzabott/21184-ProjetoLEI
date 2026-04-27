package pt.uab.musicaltrainer.config;

/**
 * Estratégia para H2 (em memória, desenvolvimento e testes).
 */
public class H2Strategy implements DatabaseStrategy {
    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }

    @Override
    public String getUrl() {
        // DB_CLOSE_DELAY=-1 mantém a BD em memória enquanto a JVM está activa.
        // Sem isto, a BD é apagada quando a última ligação fecha, o que quebra testes.
        return "jdbc:h2:mem:musicaltrainerdb;DB_CLOSE_DELAY=-1";
    }

    @Override
    public String getUsername() {
        return "sa";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getName() {
        return "H2";
    }
}
