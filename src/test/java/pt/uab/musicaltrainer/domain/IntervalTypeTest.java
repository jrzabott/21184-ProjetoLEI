package pt.uab.musicaltrainer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IntervalTypeTest {

    @Test
    void shouldHaveThirteenValues() {
        assertThat(IntervalType.values()).hasSize(13);
    }

    @Test
    void shouldReturnPerfectFifthFor7Semitones() {
        assertThat(IntervalType.fromSemitones(7).displayName()).isEqualTo("5ª Perfeita");
        assertThat(IntervalType.fromSemitones(7).semitones()).isEqualTo(7);
    }

    @Test
    void shouldReturnUnissonoFor0Semitones() {
        assertThat(IntervalType.fromSemitones(0).displayName()).isEqualTo("Uníssono");
    }

    @Test
    void shouldReturnOctaveFor12Semitones() {
        assertThat(IntervalType.fromSemitones(12).displayName()).isEqualTo("Oitava Perfeita");
    }

    @Test
    void shouldWrapAroundFor13Semitones() {
        assertThat(IntervalType.fromSemitones(13).displayName()).isEqualTo("Uníssono");
    }

    @Test
    void shouldHaveCompoundNameForTritone() {
        assertThat(IntervalType.fromSemitones(6).displayName())
            .isEqualTo("4ª Aumentada / 5ª Diminuta");
    }
}
