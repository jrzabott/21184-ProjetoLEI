package pt.uab.musicaltrainer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.MusicConstants;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.domain.ChordType;
import pt.uab.musicaltrainer.domain.Note;
import pt.uab.musicaltrainer.domain.Scale;
import pt.uab.musicaltrainer.dto.ChordQuestion;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.dto.ResultRecord;
import pt.uab.musicaltrainer.dto.ScaleQuestion;
import pt.uab.musicaltrainer.api.ResourceNotFoundException;
import pt.uab.musicaltrainer.generator.*;
import pt.uab.musicaltrainer.generator.ExerciseType;
import pt.uab.musicaltrainer.generator.GeneratorFactory;

import java.util.Arrays;

/**
 * Orquestra a geração e avaliação de exercícios.
 * Avaliação é baseada em notas MIDI tocadas pelo utilizador - sem múltipla escolha (ADR-014).
 */
@Service
public class ExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);
    private static final int MAX_REPEAT_ATTEMPTS = 5;

    private final DaoFactory daoFactory;
    private final ObjectMapper objectMapper;
    private final GeneratorFactory generatorFactory;
    private final SessionService sessionService;
    private final DifficultyService difficultyService;

    public ExerciseService(DaoFactory daoFactory, ObjectMapper objectMapper,
                           GeneratorFactory generatorFactory, SessionService sessionService,
                           DifficultyService difficultyService) {
        this.daoFactory         = daoFactory;
        this.objectMapper       = objectMapper;
        this.generatorFactory   = generatorFactory;
        this.sessionService     = sessionService;
        this.difficultyService  = difficultyService;
        logger.info("ExerciseService inicializado: tipos={}", generatorFactory.types());
    }

    /** Retrocompatibilidade - gera sem verificação de repetição. */
    public ExerciseRecord generateAndSave(String type, int difficulty) throws Exception {
        return generateAndSave(type, difficulty, null);
    }

    /**
     * Gera um exercício e guarda em BD.
     * Se sessionId fornecido e != SESSION_NONE, verifica o último exercício da sessão
     * e tenta gerar um diferente (max 5 tentativas). Se pool esgotado, aceita o repetido.
     */
    public ExerciseRecord generateAndSave(String type, int difficulty, Long sessionId) throws Exception {
        logger.debug("Gerando exercício: type={}, difficulty={}, sessionId={}", type, difficulty, sessionId);

        int suggested     = difficultyService.suggestDifficulty(type, difficulty);
        int effectiveDiff = Math.max(suggested - 2, Math.min(suggested + 2, difficulty));
        if (effectiveDiff != difficulty) {
            logger.info("Dificuldade ajustada: pedida={}, sugerida={}, efectiva={}",
                difficulty, suggested, effectiveDiff);
        }

        int lastPitchClass = getLastRootPitchClass(type, sessionId);
        ExerciseGenerator generator = getGenerator(type);
        ExerciseRecord saved = null;

        for (int attempt = 1; attempt <= MAX_REPEAT_ATTEMPTS; attempt++) {
            GeneratedExercise generated = generator.generate(effectiveDiff);
            int currentPitchClass = extractRootPitchClass(type, generated.questionJson());
            if (lastPitchClass < 0 || lastPitchClass != currentPitchClass) {
                String expectedNotesJson = toNotesJson(generated.notesToPlay());
                saved = daoFactory.createExerciseDao().save(new ExerciseRecord(
                    null, generated.type(), generated.difficulty(),
                    generated.questionJson(), expectedNotesJson, null));
                logger.info("Exercício guardado: id={}, type={}, attempt={}", saved.id(), saved.type(), attempt);
                break;
            }
            logger.debug("Exercício repetido detectado, tentativa {}/{}", attempt, MAX_REPEAT_ATTEMPTS);
        }

        if (saved == null) {
            logger.warn("Pool esgotado para type={} difficulty={} - a aceitar repetição", type, effectiveDiff);
            GeneratedExercise generated = generator.generate(effectiveDiff);
            saved = daoFactory.createExerciseDao().save(new ExerciseRecord(
                null, generated.type(), generated.difficulty(),
                generated.questionJson(), toNotesJson(generated.notesToPlay()), null));
        }

        return saved;
    }

    private int getLastRootPitchClass(String type, Long sessionId) throws Exception {
        if (sessionId == null || sessionId == MusicConstants.SESSION_NONE) return -1;
        return daoFactory.createResultDao()
            .findLastExerciseBySessionId(sessionId)
            .filter(ex -> ex.type().equals(type))
            .map(ex -> extractRootPitchClass(ex.type(), ex.question()))
            .orElse(-1);
    }

    public int getSuggestedDifficulty(String type, int currentDifficulty) throws Exception {
        return difficultyService.suggestDifficulty(type, currentDifficulty);
    }

    /**
     * Avalia a resposta do utilizador baseada nas notas MIDI tocadas.
     * Se sessionId != SESSION_NONE, persiste o resultado e actualiza os contadores da sessão.
     */
    public boolean evaluateAnswer(Long exerciseId, long sessionId, int[] userNotes) throws Exception {
        logger.debug("Avaliando: exerciseId={}, sessionId={}, notas={}",
            exerciseId, sessionId, Arrays.toString(userNotes));

        ExerciseRecord exercise = daoFactory.createExerciseDao().findById(exerciseId)
            .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado: " + exerciseId));

        int[] expectedNotes = objectMapper.readValue(exercise.correctAnswer(), int[].class);
        boolean correct = evaluate(exercise.type(), exercise.question(), expectedNotes, userNotes);

        if (sessionId != MusicConstants.SESSION_NONE) {
            if (daoFactory.createSessionDao().findById(sessionId).isEmpty()) {
                logger.warn("Sessão {} não encontrada - resultado não persistido (sessão expirada ou BD reiniciada)", sessionId);
            } else {
                String userNotesJson = toNotesJson(userNotes);
                daoFactory.createResultDao().save(
                    new ResultRecord(null, sessionId, exerciseId, userNotesJson, correct, null));
                sessionService.incrementCounters(sessionId, correct);
                logger.info("Resultado persistido: exerciseId={}, sessionId={}, correct={}",
                    exerciseId, sessionId, correct);
            }
        } else {
            logger.debug("Sandbox mode (SESSION_NONE) - resultado não persistido");
        }

        logger.info("Avaliação: exerciseId={}, type={}, correct={}", exerciseId, exercise.type(), correct);
        return correct;
    }

    /**
     * Retorna as notas esperadas para um exercício (para display/feedback).
     */
    public int[] getExpectedNotes(Long exerciseId) throws Exception {
        ExerciseRecord exercise = daoFactory.createExerciseDao().findById(exerciseId)
            .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado: " + exerciseId));
        return objectMapper.readValue(exercise.correctAnswer(), int[].class);
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
        try {
            return switch (ExerciseType.valueOf(type)) {
                case INTERVAL -> evaluateInterval(expected, user);
                case SCALE    -> evaluateScale(questionJson, user);
                case CHORD    -> evaluateChord(questionJson, expected, user);
            };
        } catch (IllegalArgumentException e) {
            logger.error("Tipo desconhecido na avaliação: {}", type);
            return false;
        }
    }

    /** INTERVAL: notas exactas (ADR-014 - treino enraizado). */
    private boolean evaluateInterval(int[] expected, int[] user) {
        if (user.length != 2) return false;
        return Arrays.equals(expected, user);
    }

    /**
     * SCALE: usa domínio directamente - Scale.get() reconstrói as notas esperadas.
     * Qualquer oitava é válida; pitch class da raiz deve coincidir.
     */
    private boolean evaluateScale(String questionJson, int[] user) {
        ScaleQuestion q;
        try {
            q = objectMapper.readValue(questionJson, ScaleQuestion.class);
        } catch (Exception e) {
            logger.error("Erro a desserializar ScaleQuestion: {}", questionJson, e);
            return false;
        }

        if (user[0] % 12 != q.root() % 12) return false;
        for (int i = 1; i < user.length; i++) {
            if (user[i] <= user[i - 1]) return false;
        }

        Scale expectedScale = Scale.get(q.type(), Note.fromMidi(user[0]));
        int[] expectedNotes = expectedScale.getNotes().stream()
            .mapToInt(Note::getMidiNumber)
            .toArray();

        int expectedCount = expectedNotes.length + 1;
        if (user.length != expectedCount) return false;
        if (user[user.length - 1] != user[0] + 12) return false;

        for (int i = 0; i < expectedNotes.length; i++) {
            if (user[i] != expectedNotes[i]) return false;
        }
        return true;
    }

    /**
     * CHORD: voicing I-III-V ascendente, qualquer oitava, mesmo pitch class na raiz (ADR-014).
     */
    private boolean evaluateChord(String questionJson, int[] expected, int[] user) {
        if (user.length != 3) return false;
        if (user[0] >= user[1] || user[1] >= user[2]) return false;
        if (expected[0] % 12 != user[0] % 12) return false;

        ChordQuestion q;
        try {
            q = objectMapper.readValue(questionJson, ChordQuestion.class);
        } catch (Exception e) {
            logger.error("Erro a desserializar ChordQuestion: {}", questionJson, e);
            return false;
        }

        int[] voicing = ChordType.valueOf(q.type()).getVoicingIntervals();
        return user[1] - user[0] == voicing[0] && user[2] - user[1] == voicing[1];
    }

    // --- Utilitários ---

    private ExerciseGenerator getGenerator(String type) {
        logger.debug("Obtendo gerador para tipo: {}", type);
        return generatorFactory.get(type);
    }

    /** Extrai o pitch class (0-11) da raiz. Package-private para teste. Retorna -1 em erro. */
    int extractRootPitchClass(String type, String questionJson) {
        try {
            return switch (ExerciseType.valueOf(type)) {
                case INTERVAL -> objectMapper.readValue(questionJson,
                    pt.uab.musicaltrainer.dto.IntervalQuestion.class).notes()[0] % 12;
                case SCALE    -> objectMapper.readValue(questionJson,
                    pt.uab.musicaltrainer.dto.ScaleQuestion.class).root() % 12;
                case CHORD    -> objectMapper.readValue(questionJson,
                    pt.uab.musicaltrainer.dto.ChordQuestion.class).root() % 12;
            };
        } catch (Exception e) {
            logger.warn("Nao foi possivel extrair pitch class de type={}: {}", type, questionJson);
            return -1;
        }
    }

    String toNotesJson(int[] notes) {
        try {
            return objectMapper.writeValueAsString(notes);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Erro a serializar array de notas", e);
            throw new RuntimeException(e);
        }
    }
}
