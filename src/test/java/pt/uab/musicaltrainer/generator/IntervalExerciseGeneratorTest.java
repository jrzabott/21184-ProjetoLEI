package pt.uab.musicaltrainer.generator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;

class IntervalExerciseGeneratorTest {

    private final IntervalExerciseGenerator generator = new IntervalExerciseGenerator();

    @Test
    void shouldReturnIntervalAsExerciseType() {
        assertThat(generator.getExerciseType()).isEqualTo("INTERVAL");
    }

    @Test
    void shouldGenerateExerciseWithTwoMidiNotes() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.notesToPlay()).hasSize(2);
        assertThat(ex.notesToPlay()[0]).isBetween(0, 127);
        assertThat(ex.notesToPlay()[1]).isBetween(0, 127);
    }

    @Test
    void shouldGenerateQuestionJsonWithNotesField() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.questionJson()).contains("\"notes\"");
        assertThat(ex.questionJson()).contains(String.valueOf(ex.notesToPlay()[0]));
        assertThat(ex.questionJson()).contains(String.valueOf(ex.notesToPlay()[1]));
    }

    @Test
    void shouldGenerateFourOptions() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.options()).hasSize(4);
    }

    @Test
    void shouldIncludeCorrectAnswerInOptions() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.options()).contains(ex.correctAnswer());
    }

    @Test
    void shouldHaveDistinctOptions() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(new HashSet<>(ex.options())).hasSameSizeAs(ex.options());
    }

    @Test
    void shouldGenerateNonBlankDescription() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.description()).isNotBlank();
    }

    @Test
    void shouldReconstructFromStoredIntervalQuestion() {
        String questionJson = "{\"notes\":[60,67]}";
        String correctAnswer = "5a Perfeita";

        GeneratedExercise ex = generator.fromStored(questionJson, correctAnswer, 3);

        assertThat(ex.notesToPlay()).containsExactly(60, 67);
        assertThat(ex.correctAnswer()).isEqualTo("5a Perfeita");
        assertThat(ex.options()).contains("5a Perfeita");
        assertThat(ex.options()).hasSize(4);
    }

    @RepeatedTest(20)
    void shouldGenerateNotesInPianoRange() {
        GeneratedExercise ex = generator.generate(5);
        assertThat(ex.notesToPlay()[0]).isBetween(21, 108);
        assertThat(ex.notesToPlay()[1]).isGreaterThan(ex.notesToPlay()[0]);
    }
}
