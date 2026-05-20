package pt.uab.musicaltrainer.e2e;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Estado partilhado entre step definitions no mesmo cenario.
 * Scope PROTOTYPE garante uma instancia nova por cenario - sem contaminacao entre testes.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ScenarioContext {

    /** Descricao do exercicio activo - para verificar que mudou apos "Proximo exercicio". */
    public String lastExerciseDescription;
}
