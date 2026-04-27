package pt.uab.musicaltrainer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.domain.ChordType;
import pt.uab.musicaltrainer.domain.ScaleType;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.generator.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Orquestra a geração e avaliação de exercícios.
 * Avaliação é baseada em notas MIDI tocadas pelo utilizador — sem múltipla escolha (ADR-014).
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
     * correct_answer guarda as notas esperadas como JSON para feedback ao utilizador.
     */
    public ExerciseRecord generateAndSave(String type, int difficulty) throws Exception {
        logger.debug("Gerando exercício: type={}, difficulty={}", type, difficulty);

        ExerciseGenerator generator = getGenerator(type);
        GeneratedExercise generated = generator.generate(difficulty);

        // ADR-014: correct_answer armazena notas esperadas como "[60,62,64]"
        String expectedNotesJson = toNotesJson(generated.notesToPlay());

        ExerciseRecord toSave = new ExerciseRecord(
            null, generated.type(), generated.difficulty(),
            generated.questionJson(), expectedNotesJson, null
        );

        ExerciseRecord saved = daoFactory.createExerciseDao().save(toSave);
        logger.info("Exercício guardado: id={}, type={}, expectedNotes={}",
            saved.id(), saved.type(), expectedNotesJson);
        return saved;
    }

    /**
     * Avalia a resposta do utilizador baseada nas notas MIDI tocadas.
     *
     * Regras por tipo (ADR-014):
     * - INTERVAL: notas exactas obrigatórias (mesmo MIDI, mesma oitava)
     * - SCALE:    padrão de semítons, qualquer oitava de partida, 8 notas (raiz→raiz)
     * - CHORD:    voicing I-III-V ascendente, qualquer oitava, mesmo pitch class na raiz
     */
    public boolean evaluateAnswer(Long exerciseId, int[] userNotes) throws Exception {
        logger.debug("Avaliando: exerciseId={}, notas={}", exerciseId, Arrays.toString(userNotes));

        Optional<ExerciseRecord> opt = daoFactory.createExerciseDao().findById(exerciseId);
        if (opt.isEmpty()) {
            logger.error("Exercício não encontrado: id={}", exerciseId);
            return false;
        }

        ExerciseRecord exercise = opt.get();
        int[] expectedNotes = parseNotesJson(exercise.correctAnswer());
        boolean correct = evaluate(exercise.type(), exercise.question(), expectedNotes, userNotes);

        logger.info("Avaliação: exerciseId={}, type={}, expected={}, got={}, correct={}",
            exerciseId, exercise.type(),
            Arrays.toString(expectedNotes), Arrays.toString(userNotes), correct);
        return correct;
    }

    /**
     * Retorna as notas esperadas para um exercício (para display/feedback).
     */
    public int[] getExpectedNotes(Long exerciseId) throws Exception {
        return daoFactory.createExerciseDao().findById(exerciseId)
            .map(e -> parseNotesJson(e.correctAnswer()))
            .orElseThrow(() -> new IllegalArgumentException("Exercício nao encontrado: " + exerciseId));
    }

    /**
     * Retorna dados de exibição para um exercício existente.
     */
    public GeneratedExercise getDisplayData(ExerciseRecord exercise) throws Exception {
        logger.debug("Reconstruindo display: exerciseId={}", exercise.id());
        ExerciseGenerator generator = getGenerator(exercise.type());
        return generator.fromStored(
            exercise.question(),
            exercise.correctAnswer(),
            exercise.difficulty()
        );
    }

    /**
     * Constrói explicação para a resposta do utilizador.
     */
    public String buildExplanation(Long exerciseId, int[] userNotes, boolean correct) throws Exception {
        int[] expected = getExpectedNotes(exerciseId);
        if (correct) {
            return "Correcto! Tocaste " + Arrays.toString(userNotes) + ".";
        }
        return "Incorrecto. Tocaste " + Arrays.toString(userNotes)
            + " mas a resposta era " + Arrays.toString(expected) + ".";
    }

    // --- Lógica de validação por tipo (ADR-014) ---

    private boolean evaluate(String type, String questionJson, int[] expected, int[] user) {
        return switch (type) {
            case "INTERVAL" -> evaluateInterval(expected, user);
            case "SCALE"    -> evaluateScale(questionJson, user);
            case "CHORD"    -> evaluateChord(questionJson, expected, user);
            default -> {
                logger.error("Tipo desconhecido na avaliação: {}", type);
                yield false;
            }
        };
    }

    /** INTERVAL: notas exactas (ADR-014 — treino enraizado). */
    private boolean evaluateInterval(int[] expected, int[] user) {
        if (user.length != 2) return false;
        return Arrays.equals(expected, user);
    }

    /**
     * SCALE: padrão de semítons, independente de oitava (ADR-014).
     * Nota count = intervalos da escala + 1 (raiz→raiz). Funciona para qualquer escala:
     * diatónica (8 notas), pentatónica (6 notas), blues (7 notas), etc.
     */
    private boolean evaluateScale(String questionJson, int[] user) {
        String scaleType = extractStringField(questionJson, "type");
        int[] expectedPattern = ScaleType.valueOf(scaleType).getSemitonePattern();
        int expectedNoteCount = expectedPattern.length + 1; // passos + 1 = notas

        if (user.length != expectedNoteCount) return false;
        for (int i = 1; i < user.length; i++) {
            if (user[i] <= user[i - 1]) return false;
        }
        if (user[user.length - 1] - user[0] != 12) return false;

        int[] userPattern = new int[user.length - 1];
        for (int i = 1; i < user.length; i++) {
            userPattern[i - 1] = user[i] - user[i - 1];
        }
        return Arrays.equals(expectedPattern, userPattern);
    }

    /**
     * CHORD: voicing I-III-V ascendente, qualquer oitava, mesmo pitch class na raiz (ADR-014).
     */
    private boolean evaluateChord(String questionJson, int[] expected, int[] user) {
        if (user.length != 3) return false;
        if (user[0] >= user[1] || user[1] >= user[2]) return false;

        // mesmo pitch class na raiz (% 12)
        if (expected[0] % 12 != user[0] % 12) return false;

        String chordType = extractStringField(questionJson, "type");
        int[] voicing = ChordType.valueOf(chordType).getVoicingIntervals();
        return user[1] - user[0] == voicing[0] && user[2] - user[1] == voicing[1];
    }

    // --- Utilitários ---

    private ExerciseGenerator getGenerator(String type) {
        ExerciseGenerator gen = generators.get(type);
        if (gen == null) {
            logger.error("Tipo de exercício desconhecido: {}", type);
            throw new IllegalArgumentException("Tipo de exercício desconhecido: " + type);
        }
        return gen;
    }

    static String toNotesJson(int[] notes) {
        return "[" + IntStream.of(notes).mapToObj(String::valueOf)
            .collect(Collectors.joining(",")) + "]";
    }

    static int[] parseNotesJson(String json) {
        String inner = json.trim().replaceAll("^\\[|\\]$", "");
        if (inner.isEmpty()) return new int[0];
        return Arrays.stream(inner.split(","))
            .mapToInt(s -> Integer.parseInt(s.trim()))
            .toArray();
    }

    private static String extractStringField(String json, String field) {
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("\"" + field + "\":\"([^\"]+)\"")
            .matcher(json);
        if (m.find()) return m.group(1);
        throw new IllegalArgumentException("Campo '" + field + "' nao encontrado em: " + json);
    }
}
