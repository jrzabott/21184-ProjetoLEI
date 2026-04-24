package pt.uab.musicaltrainer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Testes de contexto para a aplicação Musical Theory Trainer.
 * <p>
 * Verifica que o contexto Spring Boot carrega correctamente com todas
 * as configurações, dependências e beans necessários.
 *
 * @author Daniel Junior
 */
@SpringBootTest
class MusicalTrainerApplicationTest {

    /**
     * Testa se o contexto Spring Boot carrega sem erros.
     * <p>
     * Este teste de smoke verifica que todos os beans, configurações
     * e dependências estão correctamente configurados e podem ser
     * inicializados sem exceções.
     */
    @Test
    void contextLoads() {
        // Contexto carregado com sucesso
    }
}
