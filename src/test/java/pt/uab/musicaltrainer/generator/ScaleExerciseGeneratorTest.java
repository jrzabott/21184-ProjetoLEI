package pt.uab.musicaltrainer.generator;

import org.junit.jupiter.api.Test;
import pt.uab.musicaltrainer.domain.ScaleType;

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
    void shouldGenerateRootToRootNotes() {
        // ADR-014: escalas são raiz→raiz (oitava acima)
        // N notas: diatónicas=8, pentatónicas=6, blues=7. N = ScaleType.intervals.length + 1
        GeneratedExercise ex = generator.generate(1);
        int[] notes = ex.notesToPlay();

        // Independentemente do tipo, última nota = primeira + oitava
        assertThat(notes[notes.length - 1]).isEqualTo(notes[0] + 12);
    }

    @Test
    void shouldGenerateMoreThanOneNote() {
        GeneratedExercise ex = generator.generate(1);
        assertThat(ex.notesToPlay().length).isGreaterThan(1);
    }

    @Test
    void shouldGenerateCorrectMajorScalePattern() {
        GeneratedExercise ex = generator.fromStored("{\"root\":60,\"type\":\"MAJOR\"}", "MAJOR", 1);
        int[] notes = ex.notesToPlay();

        // C major: C D E F G A B C
        assertThat(notes).containsExactly(60, 62, 64, 65, 67, 69, 71, 72);
    }

    @Test
    void shouldGenerateThreeOptions() {
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

        // MAJOR tem 7 notas → raiz-a-raiz = 8. Índice [7] é válido aqui (teste específico para MAJOR)
        assertThat(ex.notesToPlay()).hasSize(8);
        assertThat(ex.notesToPlay()[0]).isEqualTo(60);
        assertThat(ex.notesToPlay()[7]).isEqualTo(72);  // C4(60) + 12 = C5(72)
        assertThat(ex.correctAnswer()).isEqualTo("MAJOR");
    }

    @Test
    void shouldGenerateNotesStartingFromRoot() {
        GeneratedExercise ex = generator.generate(1);

        String json = ex.questionJson();
        int root = Integer.parseInt(json.replaceAll(".*\"root\":(\\d+).*", "$1"));
        assertThat(ex.notesToPlay()[0]).isEqualTo(root);
    }

    @Test
    void scaleTypeSemitonePatternIsCorrectForMajor() {
        // Padrão MAJOR: W W H W W W H = [2, 2, 1, 2, 2, 2, 1]
        int[] pattern = ScaleType.MAJOR.getSemitonePattern();

        assertThat(pattern).containsExactly(2, 2, 1, 2, 2, 2, 1);
    }

    @Test
    void scaleTypeSemitonePatternIsCorrectForMinorNatural() {
        // Padrão MINOR_NATURAL: W H W W H W W = [2, 1, 2, 2, 1, 2, 2]
        int[] pattern = ScaleType.MINOR_NATURAL.getSemitonePattern();

        assertThat(pattern).containsExactly(2, 1, 2, 2, 1, 2, 2);
    }

    @Test
    void scaleTypeSemitonePatternIsCorrectForHarmonicMinor() {
        // Padrão HARMONIC_MINOR: W H W W H A H = [2, 1, 2, 2, 1, 3, 1]
        int[] pattern = ScaleType.HARMONIC_MINOR.getSemitonePattern();

        assertThat(pattern).containsExactly(2, 1, 2, 2, 1, 3, 1);
    }
}
