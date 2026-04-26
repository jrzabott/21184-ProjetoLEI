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
        return "jdbc:h2:mem:musicaltrainerdb";
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
