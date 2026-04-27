package pt.uab.musicaltrainer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.generator.*;

import java.util.Map;
import java.util.Optional;

/**
 * Orquestra a geração e avaliação de exercícios.
 * Delega geração aos generators, persistência aos DAOs.
 * Sem lógica musical aqui — toda nos generators e no domínio.
 */
@Service
public class ExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    private final DaoFactory daoFactory;
    private final Map<String, ExerciseGenerator> generators;

    public ExerciseService(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.generators = Map.of(
            "INTERVAL", new IntervalExerciseGenerator(),
            "SCALE",    new ScaleExerciseGenerator(),
            "CHORD",    new ChordExerciseGenerator()
        );
        logger.info("ExerciseService inicializado: generators={}", generators.keySet());
    }

    /**
     * Gera um exercício e guarda em BD.
     *
     * @param type tipo do exercício (INTERVAL, SCALE, CHORD)
     * @param difficulty nível 1-10
     * @return ExerciseRecord com id gerado pela BD
     */
    public ExerciseRecord generateAndSave(String type, int difficulty) throws Exception {
        logger.debug("Gerando exercício: type={}, difficulty={}", type, difficulty);

        ExerciseGenerator generator = getGenerator(type);
        GeneratedExercise generated = generator.generate(difficulty);

        ExerciseRecord toSave = new ExerciseRecord(
            null, generated.type(), generated.difficulty(),
            generated.questionJson(), generated.correctAnswer(), null
        );

        ExerciseRecord saved = daoFactory.createExerciseDao().save(toSave);
        logger.info("Exercício guardado: id={}, type={}", saved.id(), saved.type());
        return saved;
    }

    /**
     * Avalia a resposta do utilizador para um exercício.
     *
     * @param exerciseId id do exercício
     * @param userAnswer resposta do utilizador
     * @return true se correcta
     */
    public boolean evaluateAnswer(Long exerciseId, String userAnswer) throws Exception {
        logger.debug("Avaliando: exerciseId={}, answer={}", exerciseId, userAnswer);

        Optional<ExerciseRecord> opt = daoFactory.createExerciseDao().findById(exerciseId);
        if (opt.isEmpty()) {
            logger.error("Exercício não encontrado: id={}", exerciseId);
            return false;
        }

        ExerciseRecord exercise = opt.get();
        boolean correct = exercise.correctAnswer().equalsIgnoreCase(userAnswer.trim());

        logger.info("Avaliação: exerciseId={}, expected='{}', got='{}', correct={}",
            exerciseId, exercise.correctAnswer(), userAnswer, correct);
        return correct;
    }

    /**
     * Retorna dados de exibição para um exercício existente.
     * Reconstrói a partir do JSON guardado (não gera novo aleatório).
     */
    public GeneratedExercise getDisplayData(ExerciseRecord exercise) throws Exception {
        logger.debug("Reconstruindo display: exerciseId={}", exercise.id());
        ExerciseGenerator generator = getGenerator(exercise.type());
        return generator.fromStored(exercise.question(), exercise.correctAnswer(), exercise.difficulty());
    }

    /**
     * Retorna a resposta correcta de um exercício.
     */
    public String getCorrectAnswer(Long exerciseId) throws Exception {
        return daoFactory.createExerciseDao().findById(exerciseId)
            .map(ExerciseRecord::correctAnswer)
            .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado: " + exerciseId));
    }

    /**
     * Constrói explicação para a resposta do utilizador.
     */
    public String buildExplanation(Long exerciseId, String userAnswer, boolean correct) throws Exception {
        String correctAnswer = getCorrectAnswer(exerciseId);
        if (correct) {
            return "Correcto! A resposta era '" + correctAnswer + "'.";
        }
        return "Incorrecto. Respondeste '" + userAnswer + "', mas a resposta era '" + correctAnswer + "'.";
    }

    private ExerciseGenerator getGenerator(String type) {
        ExerciseGenerator gen = generators.get(type);
        if (gen == null) {
            logger.error("Tipo de exercício desconhecido: {}", type);
            throw new IllegalArgumentException("Tipo de exercício desconhecido: " + type);
        }
        return gen;
    }
}
