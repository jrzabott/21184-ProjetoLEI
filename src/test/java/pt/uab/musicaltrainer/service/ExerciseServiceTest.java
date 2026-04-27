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
        assertThat(saved.question()).contains("type");
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
    void shouldEvaluateCorrectAnswerAsTrue() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        boolean correct = service.evaluateAnswer(saved.id(), saved.correctAnswer());
        assertThat(correct).isTrue();
    }

    @Test
    void shouldEvaluateWrongAnswerAsFalse() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        boolean correct = service.evaluateAnswer(saved.id(), "OBVIOUSLY_WRONG");
        assertThat(correct).isFalse();
    }

    @Test
    void shouldReturnDisplayDataForExercise() throws Exception {
        ExerciseRecord saved = service.generateAndSave("INTERVAL", 3);
        GeneratedExercise display = service.getDisplayData(saved);

        assertThat(display.notesToPlay()).hasSize(2);
        assertThat(display.options()).hasSize(4);
        assertThat(display.description()).isNotBlank();
    }

    @Test
    void shouldReturnCorrectAnswerString() throws Exception {
        ExerciseRecord saved = service.generateAndSave("SCALE", 1);
        String answer = service.getCorrectAnswer(saved.id());

        assertThat(answer).isIn("MAJOR", "MINOR_NATURAL", "HARMONIC_MINOR");
    }

    @Test
    void shouldBuildCorrectExplanation() throws Exception {
        ExerciseRecord saved = service.generateAndSave("CHORD", 1);
        String explanation = service.buildExplanation(saved.id(), saved.correctAnswer(), true);

        assertThat(explanation).contains("Correcto").contains(saved.correctAnswer());
    }
}
