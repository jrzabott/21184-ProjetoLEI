package pt.uab.musicaltrainer.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Factory para geradores de exercícios.
 * Segue o padrão de DaoFactory — instâncias cached, geradores são stateless.
 */
@Component
public class GeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorFactory.class);

    private static final Map<String, ExerciseGenerator> GENERATORS;

    static {
        List<ExerciseGenerator> all = List.of(
            new IntervalExerciseGenerator(),
            new ScaleExerciseGenerator(),
            new ChordExerciseGenerator()
        );
        GENERATORS = all.stream()
            .collect(Collectors.toUnmodifiableMap(
                g -> g.getExerciseType().name(),
                g -> g
            ));
        logger.info("GeneratorFactory inicializado: tipos={}", GENERATORS.keySet());
    }

    /**
     * Devolve o gerador para o tipo indicado.
     *
     * @throws IllegalArgumentException se o tipo não existir
     */
    public ExerciseGenerator get(String type) {
        ExerciseGenerator gen = GENERATORS.get(type);
        if (gen == null) {
            throw new IllegalArgumentException("Tipo de exercício desconhecido: " + type);
        }
        return gen;
    }

    /** Tipos de exercício suportados. */
    public Set<String> types() {
        return GENERATORS.keySet();
    }
}
