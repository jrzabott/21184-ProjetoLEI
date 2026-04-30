package pt.uab.musicaltrainer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.ExerciseRecord;
import pt.uab.musicaltrainer.generator.GeneratedExercise;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "db.type=H2")
class ExerciseServiceTest {

    @Autowired
    private DaoFactory daoFactory;

    private ExerciseService service;

    @BeforeEach
    void setUp() {
        service = new ExerciseService(daoFactory);
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

        boolean result = service.evaluateAnswer(saved.id(), expectedNotes);

        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateCorrectScaleAsTrue() throws Exception {
        ExerciseRecord saved = service.generateAndSave("SCALE", 1);
        int[] expectedNotes = service.getExpectedNotes(saved.id());

        boolean result = service.evaluateAnswer(saved.id(), expectedNotes);

        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateCorrectChordAsTrue() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        int[] expectedNotes = service.getExpectedNotes(saved.id());

        boolean result = service.evaluateAnswer(saved.id(), expectedNotes);

        assertThat(result).isTrue();
    }

    @Test
    void shouldEvaluateWrongNotesAsFalse() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);

        boolean result = service.evaluateAnswer(saved.id(), new int[]{0, 1, 2});

        assertThat(result).isFalse();
    }

    @Test
    void shouldEvaluateWrongNumberOfNotesAsFalse() throws Exception {
        ExerciseRecord saved = service.generateAndSave("INTERVAL", 1);

        // intervalo requer exactamente 2 notas
        boolean result = service.evaluateAnswer(saved.id(), new int[]{60, 64, 67});

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

        boolean result = service.evaluateAnswer(saved.id(), transposedNotes);

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
}
