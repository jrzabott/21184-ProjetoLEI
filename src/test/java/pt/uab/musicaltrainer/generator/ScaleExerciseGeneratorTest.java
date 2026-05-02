package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pt.uab.musicaltrainer.domain.ScaleType;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ScaleExerciseGeneratorTest {

    private final ScaleExerciseGenerator generator = new ScaleExerciseGenerator(new ObjectMapper());

    @Test
    void shouldReturnScaleAsExerciseType() {
        assertThat(generator.getExerciseType()).isEqualTo(pt.uab.musicaltrainer.generator.ExerciseType.SCALE);
    }

    @Test
    void shouldGenerateQuestionJsonWithRootAndType() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.questionJson()).contains("\"root\"");
        assertThat(ex.questionJson()).contains("\"type\"");
    }

    @Test
    void shouldGenerateValidScaleType() {
        // difficulty=1 BEGINNER: apenas MAJOR disponivel
        GeneratedExercise ex = generator.generate(1);
        assertThat(ScaleType.valueOf(ex.correctAnswer()).isAlias()).isFalse();
        assertThat(ex.correctAnswer()).isEqualTo("MAJOR");
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
    void shouldGenerateOptions() {
        // P04 vai remover o campo options — por agora verifica apenas que nao esta vazio
        GeneratedExercise ex = generator.generate(1);

        assertThat(ex.options()).isNotEmpty();
    }

    @Test
    void shouldIncludeCorrectAnswerInOptions() {
        GeneratedExercise ex = generator.generate(1);

        // options contêm displayName; correctAnswer é o nome interno (enum name)
        String expectedDisplayName = ScaleType.valueOf(ex.correctAnswer()).displayName();
        assertThat(ex.options()).contains(expectedDisplayName);
    }

    @Test
    void shouldHaveDistinctOptions() {
        GeneratedExercise ex = generator.generate(1);

        assertThat(new HashSet<>(ex.options())).hasSameSizeAs(ex.options());
    }

    @Test
    void shouldGenerateMultipleScaleTypesAtElementaryDifficulty() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 300; i++) {
            seen.add(generator.generate(3).correctAnswer());
        }
        assertThat(seen).containsAnyOf("MAJOR", "MINOR_NATURAL");
        assertThat(seen.size()).isGreaterThan(1);
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

}
