package pt.uab.musicaltrainer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import pt.uab.musicaltrainer.domain.DifficultyLevel;
import pt.uab.musicaltrainer.domain.ScaleType;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ScaleExerciseGeneratorPropertyTest {

    private final ScaleExerciseGenerator generator =
        new ScaleExerciseGenerator(new ObjectMapper());

    private static final Set<String> ALIASES =
        Set.of("IONIAN", "AEOLIAN", "SUPER_LOCRIAN", "MINOR_BLUES", "BYZANTINE");

    @Property(tries = 50)
    void generatedTypeIsValidForDifficulty(@ForAll @IntRange(min = 1, max = 10) int difficulty) {
        DifficultyLevel band = DifficultyLevel.of(difficulty);
        List<String> validTypes = ScaleType.availableFor(band).stream()
            .filter(t -> !t.isAlias())
            .map(Enum::name)
            .toList();

        String type = generator.generate(difficulty).correctAnswer();

        assertThat(validTypes)
            .as("difficulty=%d band=%s should contain %s", difficulty, band, type)
            .contains(type);
    }

    @Property(tries = 50)
    void noAliasIsEverGenerated(@ForAll @IntRange(min = 1, max = 10) int difficulty) {
        String type = generator.generate(difficulty).correctAnswer();
        assertThat(ALIASES).doesNotContain(type);
    }

    @Property(tries = 30)
    void beginnerOnlyGetsMajor(@ForAll @IntRange(min = 1, max = 2) int difficulty) {
        String type = generator.generate(difficulty).correctAnswer();
        assertThat(type).isEqualTo("MAJOR");
    }

    @Property
    void advancedGetsModesEventually() {
        Set<String> seen = new java.util.HashSet<>();
        for (int i = 0; i < 300; i++) {
            seen.add(generator.generate(7).correctAnswer());
        }
        assertThat(seen).containsAnyOf("DORIAN", "PHRYGIAN", "LYDIAN", "MIXOLYDIAN", "LOCRIAN");
    }

    @Property
    void elementaryUnlocksPentatonicEventually() {
        Set<String> seen = new java.util.HashSet<>();
        for (int i = 0; i < 300; i++) {
            seen.add(generator.generate(3).correctAnswer());
        }
        assertThat(seen).containsAnyOf("MINOR_NATURAL", "PENTATONIC_MAJOR", "PENTATONIC_MINOR");
    }
}
