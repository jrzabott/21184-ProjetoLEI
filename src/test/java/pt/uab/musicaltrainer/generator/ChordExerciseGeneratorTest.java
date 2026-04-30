package pt.uab.musicaltrainer.generator;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ChordExerciseGeneratorTest {

    private final ChordExerciseGenerator generator = new ChordExerciseGenerator();

    @Test
    void shouldReturnChordAsExerciseType() {
        assertThat(generator.getExerciseType()).isEqualTo(pt.uab.musicaltrainer.generator.ExerciseType.CHORD);
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
        // 4 tipos de acorde disponíveis a difficulty=8 (ADVANCED)
        GeneratedExercise ex = generator.generate(8);

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
            seen.add(generator.generate(8).correctAnswer());
        }
        assertThat(seen).containsExactlyInAnyOrder("MAJOR", "MINOR", "DIMINISHED", "AUGMENTED");
    }

    @Test
    void shouldReconstructMajorChordFromStoredQuestion() {
        // C maior: C4(60) + E4(64) + G4(67)
        // difficulty=8 (ADVANCED) para ter os 4 tipos disponíveis como opções
        String questionJson = "{\"root\":60,\"type\":\"MAJOR\"}";

        GeneratedExercise ex = generator.fromStored(questionJson, "MAJOR", 8);

        assertThat(ex.notesToPlay()).hasSize(3);
        assertThat(ex.notesToPlay()[0]).isEqualTo(60);
        assertThat(ex.correctAnswer()).isEqualTo("MAJOR");
        assertThat(ex.options()).hasSize(4);
    }

    @Test
    void chordTypeVoicingIntervalsAreCorrect() {
        // ADR-014: validação I-III-V por diferença de intervalos consecutivos
        assertThat(pt.uab.musicaltrainer.domain.ChordType.MAJOR.getVoicingIntervals())
            .containsExactly(4, 3);
        assertThat(pt.uab.musicaltrainer.domain.ChordType.MINOR.getVoicingIntervals())
            .containsExactly(3, 4);
        assertThat(pt.uab.musicaltrainer.domain.ChordType.DIMINISHED.getVoicingIntervals())
            .containsExactly(3, 3);
        assertThat(pt.uab.musicaltrainer.domain.ChordType.AUGMENTED.getVoicingIntervals())
            .containsExactly(4, 4);
    }

    @Test
    void shouldAcceptMajorChordInAnyOctave() {
        // ADR-014: qualquer oitava é válida para acordes — validação por padrão de intervalos
        GeneratedExercise rootOctave = generator.fromStored("{\"root\":60,\"type\":\"MAJOR\"}", "MAJOR", 1);
        GeneratedExercise higherOctave = generator.fromStored("{\"root\":72,\"type\":\"MAJOR\"}", "MAJOR", 1);

        // Padrão [4,3] deve ser o mesmo em qualquer oitava
        int[] low = rootOctave.notesToPlay();
        int[] high = higherOctave.notesToPlay();
        assertThat(low[1] - low[0]).isEqualTo(high[1] - high[0]);
        assertThat(low[2] - low[1]).isEqualTo(high[2] - high[1]);
    }

    @Test
    void shouldGenerateNotesStartingFromRoot() {
        GeneratedExercise ex = generator.generate(1);

        String json = ex.questionJson();
        int root = Integer.parseInt(json.replaceAll(".*\"root\":(\\d+).*", "$1"));
        assertThat(ex.notesToPlay()[0]).isEqualTo(root);
    }
}
