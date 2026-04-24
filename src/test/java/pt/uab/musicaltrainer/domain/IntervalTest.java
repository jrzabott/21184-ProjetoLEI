package pt.uab.musicaltrainer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes para a classe Interval (modelo de domínio).
 * <p>
 * Valida a identificação correta de intervalos entre duas notas,
 * incluindo nome completo (ex: 5ª Perfeita) e número de semítons.
 *
 * @author Daniel Junior
 */
class IntervalTest {

    /**
     * Testa intervalo C4 para G4 — deve ser 5ª Perfeita (7 semítons).
     * Este é o exemplo do critério de aceitação F01.
     */
    @Test
    void shouldIdentifyPerfectFifthFromC4ToG4() {
        Note c4 = Note.fromMidi(60);
        Note g4 = Note.fromMidi(67);

        Interval interval = Interval.between(c4, g4);

        assertThat(interval.getName()).isEqualTo("5ª Perfeita");
        assertThat(interval.getSemitones()).isEqualTo(7);
    }

    /**
     * Testa intervalo C4 para E4 — deve ser 3ª Maior (4 semítons).
     */
    @Test
    void shouldIdentifyMajorThirdFromC4ToE4() {
        Note c4 = Note.fromMidi(60);
        Note e4 = Note.fromMidi(64);

        Interval interval = Interval.between(c4, e4);

        assertThat(interval.getName()).isEqualTo("3ª Maior");
        assertThat(interval.getSemitones()).isEqualTo(4);
    }

    /**
     * Testa intervalo C4 para D4 — deve ser 2ª Maior (2 semítons).
     */
    @Test
    void shouldIdentifyMajorSecondFromC4ToD4() {
        Note c4 = Note.fromMidi(60);
        Note d4 = Note.fromMidi(62);

        Interval interval = Interval.between(c4, d4);

        assertThat(interval.getName()).isEqualTo("2ª Maior");
        assertThat(interval.getSemitones()).isEqualTo(2);
    }

    /**
     * Testa intervalo C4 para C#4 — deve ser 2ª Menor (1 semítom).
     */
    @Test
    void shouldIdentifyMinorSecondFromC4ToCs4() {
        Note c4 = Note.fromMidi(60);
        Note cs4 = Note.fromMidi(61);

        Interval interval = Interval.between(c4, cs4);

        assertThat(interval.getName()).isEqualTo("2ª Menor");
        assertThat(interval.getSemitones()).isEqualTo(1);
    }

    /**
     * Testa intervalo C4 para F4 — deve ser 4ª Perfeita (5 semítons).
     */
    @Test
    void shouldIdentifyPerfectFourthFromC4ToF4() {
        Note c4 = Note.fromMidi(60);
        Note f4 = Note.fromMidi(65);

        Interval interval = Interval.between(c4, f4);

        assertThat(interval.getName()).isEqualTo("4ª Perfeita");
        assertThat(interval.getSemitones()).isEqualTo(5);
    }

    /**
     * Testa intervalo C4 para A4 — deve ser 6ª Maior (9 semítons).
     */
    @Test
    void shouldIdentifyMajorSixthFromC4ToA4() {
        Note c4 = Note.fromMidi(60);
        Note a4 = Note.fromMidi(69);

        Interval interval = Interval.between(c4, a4);

        assertThat(interval.getName()).isEqualTo("6ª Maior");
        assertThat(interval.getSemitones()).isEqualTo(9);
    }

    /**
     * Testa intervalo C4 para B4 — deve ser 7ª Maior (11 semítons).
     */
    @Test
    void shouldIdentifyMajorSeventhFromC4ToB4() {
        Note c4 = Note.fromMidi(60);
        Note b4 = Note.fromMidi(71);

        Interval interval = Interval.between(c4, b4);

        assertThat(interval.getName()).isEqualTo("7ª Maior");
        assertThat(interval.getSemitones()).isEqualTo(11);
    }

    /**
     * Testa intervalo C4 para C5 — deve ser Oitava Perfeita (12 semítons).
     */
    @Test
    void shouldIdentifyPerfectOctaveFromC4ToC5() {
        Note c4 = Note.fromMidi(60);
        Note c5 = Note.fromMidi(72);

        Interval interval = Interval.between(c4, c5);

        assertThat(interval.getName()).isEqualTo("Oitava Perfeita");
        assertThat(interval.getSemitones()).isEqualTo(12);
    }

    /**
     * Testa intervalo uníssono (mesma nota) — deve ser Uníssono (0 semítons).
     */
    @Test
    void shouldIdentifyUnissonFromC4ToC4() {
        Note c4 = Note.fromMidi(60);
        Note c4Again = Note.fromMidi(60);

        Interval interval = Interval.between(c4, c4Again);

        assertThat(interval.getName()).isEqualTo("Uníssono");
        assertThat(interval.getSemitones()).isEqualTo(0);
    }

    /**
     * Testa intervalo descendente — G4 para C4 (descendente).
     * Deve retornar a mesma distância em semítons (7).
     */
    @Test
    void shouldHandleDescendingInterval() {
        Note g4 = Note.fromMidi(67);
        Note c4 = Note.fromMidi(60);

        Interval interval = Interval.between(g4, c4);

        assertThat(interval.getSemitones()).isEqualTo(7);
    }
}
