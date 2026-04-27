package pt.uab.musicaltrainer.generator;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ScaleExerciseGeneratorTest {

    private final ScaleExerciseGenerator generator = new ScaleExerciseGenerator();

    @Test
    void shouldReturnScaleAsExerciseType() {
        assertThat(generator.getExerciseType()).isEqualTo("SCALE");
    }

    @Test
    void shouldGenerateQuestionJsonWithRootAndType() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.questionJson()).contains("\"root\"");
        assertThat(ex.questionJson()).contains("\"type\"");
    }

    @Test
    void shouldGenerateValidScaleType() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.correctAnswer()).isIn("MAJOR", "MINOR_NATURAL", "HARMONIC_MINOR");
    }

    @Test
    void shouldGenerateSevenNotesForScale() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.notesToPlay()).hasSize(7);
    }

    @Test
    void shouldGenerateThreeOptions() {
        // MVP tem 3 tipos de escala — logo 3 opções
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.options()).hasSize(3);
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
    void shouldGenerateAllThreeScaleTypesEventually() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            seen.add(generator.generate(5).correctAnswer());
        }
        assertThat(seen).containsExactlyInAnyOrder("MAJOR", "MINOR_NATURAL", "HARMONIC_MINOR");
    }

    @Test
    void shouldReconstructFromStoredScaleQuestion() {
        String questionJson = "{\"root\":60,\"type\":\"MAJOR\"}";

        GeneratedExercise ex = generator.fromStored(questionJson, "MAJOR", 1);

        assertThat(ex.notesToPlay()).hasSize(7);
        assertThat(ex.notesToPlay()[0]).isEqualTo(60);  // raiz deve ser C4
        assertThat(ex.correctAnswer()).isEqualTo("MAJOR");
        assertThat(ex.options()).contains("MAJOR");
    }

    @Test
    void shouldGenerateNotesStartingFromRoot() {
        GeneratedExercise ex = generator.generate(1);

        // A primeira nota deve ser a raiz (MIDI da raiz)
        String json = ex.questionJson();
        int root = Integer.parseInt(json.replaceAll(".*\"root\":(\\d+).*", "$1"));
        assertThat(ex.notesToPlay()[0]).isEqualTo(root);
    }
}
