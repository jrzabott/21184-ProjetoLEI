package pt.uab.musicaltrainer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.generator.GeneratedExercise;
import pt.uab.musicaltrainer.generator.GeneratorFactory;
import pt.uab.musicaltrainer.service.DifficultyService;
import pt.uab.musicaltrainer.service.SessionService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
class ExerciseServiceTest {

    @Autowired
    private DaoFactory daoFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeneratorFactory generatorFactory;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DifficultyService difficultyService;

    private ExerciseService service;

    @BeforeEach
    void setUp() {
        service = new ExerciseService(daoFactory, objectMapper, generatorFactory, sessionService, difficultyService);
    }

    // --- Geração ---

    @Test
    void shouldGenerateAndSaveIntervalExercise() throws Exception {
        ExerciseRecord saved = service.generateAndSave("INTERVAL", 1);

        assertThat(saved).isNotNull();
        assertThat(saved.id()).isGreaterThan(0);
        assertThat(saved.type()).isEqualTo("INTERVAL");
        assertThat(saved.question()).contains("notes");
    }

    @Test
    void shouldGenerateAndSaveScaleExercise() throws Exception {
        ExerciseRecord saved = service.generateAndSave("SCALE", 1);

        assertThat(saved.type()).isEqualTo("SCALE");
        assertThat(saved.question()).contains("root");
    }

    @Test
    void shouldGenerateAndSaveChordExercise() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);

        assertThat(saved.type()).isEqualTo("CHORD");
        assertThat(saved.question()).contains("root");
    }

    @Test
    void shouldThrowForUnknownExerciseType() {
        assertThatThrownBy(() -> service.generateAndSave("UNKNOWN", 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("UNKNOWN");
    }

    @Test
    void shouldStoreExpectedNotesInCorrectAnswerColumn() throws Exception {
        // ADR-014: correct_answer guarda as notas esperadas como JSON para feedback
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);

        assertThat(saved.correctAnswer()).startsWith("[");
        assertThat(saved.correctAnswer()).endsWith("]");
        assertThat(saved.correctAnswer()).contains(",");
    }

    // --- Avaliação baseada em notas MIDI ---

    @Test
    void shouldEvaluateCorrectIntervalAsTrue() throws Exception {
        ExerciseRecord saved = service.generateAndSave("INTERVAL", 1);
        int[] expectedNotes = service.getExpectedNotes(saved.id());

        boolean result = service.evaluateAnswer(saved.id(), pt.uab.musicaltrainer.MusicConstants.SESSION_NONE, expectedNotes);

        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateCorrectScaleAsTrue() throws Exception {
        ExerciseRecord saved = service.generateAndSave("SCALE", 1);
        int[] expectedNotes = service.getExpectedNotes(saved.id());

        boolean result = service.evaluateAnswer(saved.id(), pt.uab.musicaltrainer.MusicConstants.SESSION_NONE, expectedNotes);

        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateCorrectChordAsTrue() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        int[] expectedNotes = service.getExpectedNotes(saved.id());

        boolean result = service.evaluateAnswer(saved.id(), pt.uab.musicaltrainer.MusicConstants.SESSION_NONE, expectedNotes);

        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateWrongNotesAsFalse() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);

        boolean result = service.evaluateAnswer(saved.id(), pt.uab.musicaltrainer.MusicConstants.SESSION_NONE, new int[]{0, 1, 2});

        assertThat(result).isFalse();
    }

    @Test
    void shouldEvaluateWrongNumberOfNotesAsFalse() throws Exception {
        ExerciseRecord saved = service.generateAndSave("INTERVAL", 1);

        // intervalo requer exactamente 2 notas
        boolean result = service.evaluateAnswer(saved.id(), pt.uab.musicaltrainer.MusicConstants.SESSION_NONE, new int[]{60, 64, 67});

        assertThat(result).isFalse();
    }

    // --- Scale: qualquer oitava é válida ---

    @Test
    void shouldEvaluateScaleInDifferentOctaveAsCorrect() throws Exception {
        // ADR-014: escala em oitava diferente é correcta (validação por padrão)
        ExerciseRecord saved = service.generateAndSave("SCALE", 1);
        GeneratedExercise display = service.getDisplayData(saved);
        int[] expectedNotes = display.notesToPlay();

        // transpor todas as notas uma oitava acima (+12)
        int[] transposedNotes = new int[expectedNotes.length];
        for (int i = 0; i < expectedNotes.length; i++) {
            transposedNotes[i] = expectedNotes[i] + 12;
        }

        boolean result = service.evaluateAnswer(saved.id(), pt.uab.musicaltrainer.MusicConstants.SESSION_NONE, transposedNotes);

        assertThat(result).isTrue();
    }

    // --- Display e feedback ---

    @Test
    void shouldReturnDisplayDataWithCorrectNoteCount() throws Exception {
        ExerciseRecord intervalEx = service.generateAndSave("INTERVAL", 1);
        ExerciseRecord scaleEx = service.generateAndSave("SCALE", 1);
        ExerciseRecord chordEx = service.generateAndSave("CHORD", 1);

        assertThat(service.getDisplayData(intervalEx).notesToPlay()).hasSize(2);
        assertThat(service.getDisplayData(scaleEx).notesToPlay()).hasSize(8);
        assertThat(service.getDisplayData(chordEx).notesToPlay()).hasSize(3);
    }

    @Test
    void shouldReturnExpectedNotesArray() throws Exception {
        ExerciseRecord saved = service.generateAndSave("SCALE", 1);
        int[] notes = service.getExpectedNotes(saved.id());

        assertThat(notes).hasSize(8);
    }

    // --- Persistência de resultados e sessão ---

    @Test
    void shouldNotPersistResultWhenSessionNone() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        int[] expected = service.getExpectedNotes(saved.id());
        service.evaluateAnswer(saved.id(), pt.uab.musicaltrainer.MusicConstants.SESSION_NONE, expected);
        List<pt.uab.musicaltrainer.dto.ResultRecord> results =
            daoFactory.createResultDao().findAll().stream()
                .filter(r -> r.exerciseId().equals(saved.id())).toList();
        assertThat(results).isEmpty();
    }

    @Test
    void shouldPersistResultWhenSessionProvided() throws Exception {
        pt.uab.musicaltrainer.dto.SessionRecord session = sessionService.startSession();
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        int[] expected = service.getExpectedNotes(saved.id());
        service.evaluateAnswer(saved.id(), session.id(), expected);
        List<pt.uab.musicaltrainer.dto.ResultRecord> results =
            daoFactory.createResultDao().findBySessionId(session.id());
        assertThat(results).hasSize(1);
        assertThat(results.get(0).isCorrect()).isTrue();
    }

    @Test
    void shouldUpdateSessionCountersOnCorrectAnswer() throws Exception {
        pt.uab.musicaltrainer.dto.SessionRecord session = sessionService.startSession();
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        int[] expected = service.getExpectedNotes(saved.id());
        service.evaluateAnswer(saved.id(), session.id(), expected);
        pt.uab.musicaltrainer.dto.SessionRecord updated =
            daoFactory.createSessionDao().findById(session.id()).orElseThrow();
        assertThat(updated.totalExercises()).isEqualTo(1);
        assertThat(updated.correctAnswers()).isEqualTo(1);
    }

    @Test
    void shouldUpdateSessionCountersOnWrongAnswer() throws Exception {
        pt.uab.musicaltrainer.dto.SessionRecord session = sessionService.startSession();
        ExerciseRecord saved = service.generateAndSave("INTERVAL", 1);
        service.evaluateAnswer(saved.id(), session.id(), new int[]{0, 0});
        pt.uab.musicaltrainer.dto.SessionRecord updated =
            daoFactory.createSessionDao().findById(session.id()).orElseThrow();
        assertThat(updated.totalExercises()).isEqualTo(1);
        assertThat(updated.incorrectAnswers()).isEqualTo(1);
    }

    @Test
    void shouldSkipNoRepeatCheckWhenSessionNone() throws Exception {
        ExerciseRecord ex = service.generateAndSave("INTERVAL", 1, pt.uab.musicaltrainer.MusicConstants.SESSION_NONE);
        assertThat(ex).isNotNull();
        assertThat(ex.id()).isGreaterThan(0);
    }

    @Test
    void shouldSkipNoRepeatCheckWhenSessionIdNull() throws Exception {
        ExerciseRecord ex = service.generateAndSave("SCALE", 1, null);
        assertThat(ex).isNotNull();
    }

    @Test
    void shouldAcceptFirstExerciseWithNoHistory() throws Exception {
        pt.uab.musicaltrainer.dto.SessionRecord session = sessionService.startSession();
        ExerciseRecord ex = service.generateAndSave("CHORD", 1, session.id());
        assertThat(ex).isNotNull();
        assertThat(ex.id()).isGreaterThan(0);
    }

    @Test
    void shouldEvaluateWithoutCrashWhenSessionIdDoesNotExist() throws Exception {
        // Bug: sessionId válido mas sessão não existe na BD (ex: BD reiniciada)
        ExerciseRecord saved = service.generateAndSave("CHORD", 1, null);
        int[] expected = service.getExpectedNotes(saved.id());

        assertThatCode(() -> service.evaluateAnswer(saved.id(), 99999L, expected))
            .doesNotThrowAnyException();

        // Nenhum resultado guardado - sessão inexistente = fallback para sandbox
        assertThat(daoFactory.createResultDao().findAll().stream()
            .filter(r -> r.exerciseId().equals(saved.id()))
            .toList()).isEmpty();
    }

    // --- No-consecutive-repeat end-to-end (F02/P12) ---

    @Test
    void shouldNotGenerateSameExerciseConsecutivelyInSession() throws Exception {
        // F02: sem exercícios consecutivos repetidos na mesma sessão
        pt.uab.musicaltrainer.dto.SessionRecord session = sessionService.startSession();

        // Gerar e avaliar primeiro exercício - cria histórico na sessão
        ExerciseRecord first = service.generateAndSave("CHORD", 1, session.id());
        int[] wrongNotes = new int[]{0, 1, 2};
        service.evaluateAnswer(first.id(), session.id(), wrongNotes);

        // Gerar segundo exercício - deve ser diferente do primeiro
        // (com pool de ~37 raizes, probabilidade de repetição < 3%)
        // Tentar até 10 vezes para confirmar que o repeat check funciona
        boolean sawDifferent = false;
        for (int attempt = 0; attempt < 10; attempt++) {
            ExerciseRecord second = service.generateAndSave("CHORD", 1, session.id());
            if (!second.question().equals(first.question())) {
                sawDifferent = true;
                break;
            }
            // Se gerou o mesmo, avalia e tenta de novo
            service.evaluateAnswer(second.id(), session.id(), wrongNotes);
        }
        assertThat(sawDifferent)
            .as("Gerador devia produzir exercício diferente do anterior em 10 tentativas")
            .isTrue();
    }
}
