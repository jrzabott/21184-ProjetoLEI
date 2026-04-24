package pt.uab.musicaltrainer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes para a classe Scale (modelo de domínio).
 * <p>
 * Valida a geração correcta de escalas musicais a partir de uma nota raiz
 * e tipo de escala (Maior, Menor Natural, Menor Harmónica, Dorian, etc.).
 *
 * @author Daniel Junior
 */
class ScaleTest {

    /**
     * Testa escala Maior (C Major) — C D E F G A B.
     * C=60, D=62, E=64, F=65, G=67, A=69, B=71
     */
    @Test
    void shouldGenerateCMajorScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("MAJOR", c4);

        assertThat(scale.getType()).isEqualTo("MAJOR");
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(71)   // B
        );
    }

    /**
     * Testa escala Menor Natural (A Natural Minor) — A B C D E F G.
     * A=57, B=59, C=60, D=62, E=64, F=65, G=67
     */
    @Test
    void shouldGenerateANaturalMinorScale() {
        Note a3 = Note.fromMidi(57);
        Scale scale = Scale.get("MINOR_NATURAL", a3);

        assertThat(scale.getType()).isEqualTo("MINOR_NATURAL");
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(57),  // A
            Note.fromMidi(59),  // B
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(67)   // G
        );
    }

    /**
     * Testa escala Menor Harmónica (A Harmonic Minor) — A B C D E F G#.
     * A=57, B=59, C=60, D=62, E=64, F=65, G#=68
     */
    @Test
    void shouldGenerateAHarmonicMinorScale() {
        Note a3 = Note.fromMidi(57);
        Scale scale = Scale.get("HARMONIC_MINOR", a3);

        assertThat(scale.getType()).isEqualTo("HARMONIC_MINOR");
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(57),  // A
            Note.fromMidi(59),  // B
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(68)   // G#
        );
    }

    /**
     * Testa escala Dorian (C Dorian) — C D Eb F G A Bb.
     * C=60, D=62, Eb=63, F=65, G=67, A=69, Bb=70
     */
    @Test
    void shouldGenerateCDorianScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("DORIAN", c4);

        assertThat(scale.getType()).isEqualTo("DORIAN");
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(63),  // Eb
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(70)   // Bb
        );
    }

    /**
     * Testa escala Pentatónica Menor (A Pentatonic Minor) — A C D E G.
     * A=57, C=60, D=62, E=64, G=67
     */
    @Test
    void shouldGenerateAMinorPentatonicScale() {
        Note a3 = Note.fromMidi(57);
        Scale scale = Scale.get("PENTATONIC_MINOR", a3);

        assertThat(scale.getType()).isEqualTo("PENTATONIC_MINOR");
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(57),  // A
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(67)   // G
        );
    }

    /**
     * Testa escala Blues (C Blues) — C Eb F Gb G Bb.
     * C=60, Eb=63, F=65, Gb=66, G=67, Bb=70
     */
    @Test
    void shouldGenerateCBluesScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("BLUES", c4);

        assertThat(scale.getType()).isEqualTo("BLUES");
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(63),  // Eb
            Note.fromMidi(65),  // F
            Note.fromMidi(66),  // Gb
            Note.fromMidi(67),  // G
            Note.fromMidi(70)   // Bb
        );
    }

    /**
     * Testa que escalas diferentes produzem notas diferentes.
     */
    @Test
    void shouldDifferentiateBetweenScaleTypes() {
        Note c4 = Note.fromMidi(60);
        Scale majorScale = Scale.get("MAJOR", c4);
        Scale dorianScale = Scale.get("DORIAN", c4);

        assertThat(majorScale.getNotes()).isNotEqualTo(dorianScale.getNotes());
    }

    /**
     * Testa que escalas geradas a partir de raízes diferentes têm notas diferentes.
     */
    @Test
    void shouldDifferentiateBetweenRoots() {
        Note c4 = Note.fromMidi(60);
        Note d4 = Note.fromMidi(62);
        Scale cMajor = Scale.get("MAJOR", c4);
        Scale dMajor = Scale.get("MAJOR", d4);

        assertThat(cMajor.getNotes()).isNotEqualTo(dMajor.getNotes());
    }
}
