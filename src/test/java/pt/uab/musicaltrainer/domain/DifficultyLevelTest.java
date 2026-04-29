package pt.uab.musicaltrainer.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DifficultyLevelTest {

    @Test
    void shouldHaveFiveLevels() {
        assertThat(DifficultyLevel.values()).hasSize(5);
    }

    @Test
    void shouldMapNumericValuesToCorrectLevel() {
        assertThat(DifficultyLevel.of(1)).isEqualTo(DifficultyLevel.BEGINNER);
        assertThat(DifficultyLevel.of(2)).isEqualTo(DifficultyLevel.BEGINNER);
        assertThat(DifficultyLevel.of(3)).isEqualTo(DifficultyLevel.ELEMENTARY);
        assertThat(DifficultyLevel.of(4)).isEqualTo(DifficultyLevel.ELEMENTARY);
        assertThat(DifficultyLevel.of(5)).isEqualTo(DifficultyLevel.INTERMEDIATE);
        assertThat(DifficultyLevel.of(6)).isEqualTo(DifficultyLevel.INTERMEDIATE);
        assertThat(DifficultyLevel.of(7)).isEqualTo(DifficultyLevel.ADVANCED);
        assertThat(DifficultyLevel.of(8)).isEqualTo(DifficultyLevel.ADVANCED);
        assertThat(DifficultyLevel.of(9)).isEqualTo(DifficultyLevel.EXPERT);
        assertThat(DifficultyLevel.of(10)).isEqualTo(DifficultyLevel.EXPERT);
    }

    @Test
    void shouldReturnLowerBound() {
        assertThat(DifficultyLevel.BEGINNER.lowerBound()).isEqualTo(1);
        assertThat(DifficultyLevel.EXPERT.lowerBound()).isEqualTo(9);
    }

    @Test
    void shouldClampOutOfRangeValues() {
        assertThat(DifficultyLevel.of(0)).isEqualTo(DifficultyLevel.BEGINNER);
        assertThat(DifficultyLevel.of(11)).isEqualTo(DifficultyLevel.EXPERT);
    }
}
