package pt.uab.musicaltrainer.generator;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ChordExerciseGeneratorTest {

    private final ChordExerciseGenerator generator = new ChordExerciseGenerator();

    @Test
    void shouldReturnChordAsExerciseType() {
        assertThat(generator.getExerciseType()).isEqualTo("CHORD");
    }

    @Test
    void shouldGenerateQuestionJsonWithRootAndType() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.questionJson()).contains("\"root\"");
        assertThat(ex.questionJson()).contains("\"type\"");
    }

    @Test
    void shouldGenerateValidChordType() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.correctAnswer()).isIn("MAJOR", "MINOR", "DIMINISHED", "AUGMENTED");
    }

    @Test
    void shouldGenerateThreeNotesForTriad() {
        GeneratedExercise ex = generator.generate(1);

        // Tríades têm 3 notas
        assertThat(ex.notesToPlay()).hasSize(3);
    }

    @Test
    void shouldGenerateFourOptions() {
        // 4 tipos de acorde no MVP
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
    void shouldGenerateAllFourChordTypesEventually() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            seen.add(generator.generate(5).correctAnswer());
        }
        assertThat(seen).containsExactlyInAnyOrder("MAJOR", "MINOR", "DIMINISHED", "AUGMENTED");
    }

    @Test
    void shouldReconstructMajorChordFromStoredQuestion() {
        // C maior: C4(60) + E4(64) + G4(67)
        String questionJson = "{\"root\":60,\"type\":\"MAJOR\"}";

        GeneratedExercise ex = generator.fromStored(questionJson, "MAJOR", 1);

        assertThat(ex.notesToPlay()).hasSize(3);
        assertThat(ex.notesToPlay()[0]).isEqualTo(60);
        assertThat(ex.correctAnswer()).isEqualTo("MAJOR");
        assertThat(ex.options()).hasSize(4);
    }

    @Test
    void shouldGenerateNotesStartingFromRoot() {
        GeneratedExercise ex = generator.generate(1);

        String json = ex.questionJson();
        int root = Integer.parseInt(json.replaceAll(".*\"root\":(\\d+).*", "$1"));
        assertThat(ex.notesToPlay()[0]).isEqualTo(root);
    }
}
