package pt.uab.musicaltrainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação principal do Musical Theory Trainer.
 * <p>
 * Configura e arranca o servidor Spring Boot com suporte a:
 * <ul>
 * <li>Base de dados em memória H2 para desenvolvimento</li>
 * <li>Testes automáticos com JUnit 5 e AssertJ</li>
 * <li>Inicialização automática do schema SQL</li>
 * </ul>
 *
 * @author Daniel Junior
 * @version 1.0.0
 */
@SpringBootApplication
public class MusicalTrainerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicalTrainerApplication.class, args);
    }
}
