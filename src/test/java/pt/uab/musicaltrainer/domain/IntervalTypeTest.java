package pt.uab.musicaltrainer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IntervalTypeTest {

    @Test
    void shouldHaveThirteenValues() {
        assertThat(IntervalType.values()).hasSize(13);
    }

    @Test
    void internalNameIsAsciiSafe() {
        // Regra de projecto: internalName() nunca contém ª, º ou acentos
        assertThat(IntervalType.fromSemitones(7).internalName()).isEqualTo("5a Perfeita");
        assertThat(IntervalType.fromSemitones(0).internalName()).isEqualTo("Unissono");
        assertThat(IntervalType.fromSemitones(6).internalName())
            .isEqualTo("4a Aumentada / 5a Diminuta");
    }

    @Test
    void displayNameHasFullPortuguese() {
        // displayName() pode ter caracteres especiais — apenas para frontend
        assertThat(IntervalType.fromSemitones(7).displayName()).isEqualTo("5ª Perfeita");
        assertThat(IntervalType.fromSemitones(0).displayName()).isEqualTo("Uníssono");
        assertThat(IntervalType.fromSemitones(6).displayName())
            .isEqualTo("4ª Aumentada / 5ª Diminuta");
    }

    @Test
    void shouldReturnOctaveFor12Semitones() {
        assertThat(IntervalType.fromSemitones(12).internalName()).isEqualTo("Oitava Perfeita");
        assertThat(IntervalType.fromSemitones(12).semitones()).isEqualTo(12);
    }

    @Test
    void shouldWrapAroundFor13Semitones() {
        assertThat(IntervalType.fromSemitones(13).internalName()).isEqualTo("Unissono");
    }
}
