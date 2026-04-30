package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Factory para geradores de exercícios.
 * Recebe ObjectMapper via Spring DI e passa-o a cada gerador — sem instâncias static.
 */
@Component
public class GeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorFactory.class);

    private final Map<String, ExerciseGenerator> generators;

    public GeneratorFactory(ObjectMapper objectMapper) {
        List<ExerciseGenerator> all = List.of(
            new IntervalExerciseGenerator(objectMapper),
            new ScaleExerciseGenerator(objectMapper),
            new ChordExerciseGenerator(objectMapper)
        );
        generators = all.stream()
            .collect(Collectors.toUnmodifiableMap(
                g -> g.getExerciseType().name(),
                g -> g
            ));
        logger.info("GeneratorFactory inicializado: tipos={}", generators.keySet());
    }

    public ExerciseGenerator get(String type) {
        ExerciseGenerator gen = generators.get(type);
        if (gen == null) {
            throw new IllegalArgumentException("Tipo de exercício desconhecido: " + type);
        }
        return gen;
    }

    public Set<String> types() {
        return generators.keySet();
    }
}
