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

    // Modos diatónicos (Modes of the Major Scale)
    @Test
    void shouldGenerateMelodicMinorScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("MELODIC_MINOR", c4);
        // C Melodic Minor: C D Eb F G A B
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(63),  // Eb
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(71)   // B
        );
    }

    @Test
    void shouldGeneratePhrygianScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("PHRYGIAN", c4);
        // C Phrygian: C Db Eb F G Ab Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // Db
            Note.fromMidi(63),  // Eb
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(68),  // Ab
            Note.fromMidi(70)   // Bb
        );
    }

    @Test
    void shouldGenerateLydianScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("LYDIAN", c4);
        // C Lydian: C D E F# G A B
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(66),  // F#
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(71)   // B
        );
    }

    @Test
    void shouldGenerateMixolydianScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("MIXOLYDIAN", c4);
        // C Mixolydian: C D E F G A Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(70)   // Bb
        );
    }

    @Test
    void shouldGenerateLocrianScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("LOCRIAN", c4);
        // C Locrian: C Db Eb F Gb Ab Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // Db
            Note.fromMidi(63),  // Eb
            Note.fromMidi(65),  // F
            Note.fromMidi(66),  // Gb
            Note.fromMidi(68),  // Ab
            Note.fromMidi(70)   // Bb
        );
    }

    // Escalas menores alteradas (Altered Minor Scales)
    @Test
    void shouldGeneratePhrygianDominantScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("PHRYGIAN_DOMINANT", c4);
        // C Phrygian Dominant: C Db E F G Ab Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // Db
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(68),  // Ab
            Note.fromMidi(70)   // Bb
        );
    }

    @Test
    void shouldGenerateLydianDominantScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("LYDIAN_DOMINANT", c4);
        // C Lydian Dominant: C D E F# G A Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(66),  // F#
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(70)   // Bb
        );
    }

    @Test
    void shouldGenerateDorianFlat2Scale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("DORIAN_FLAT_2", c4);
        // C Dorian b2: C Db Eb F G A Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // Db
            Note.fromMidi(63),  // Eb
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(70)   // Bb
        );
    }

    @Test
    void shouldGenerateLocrianNatural2Scale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("LOCRIAN_NATURAL_2", c4);
        // C Locrian Natural 2: C D Eb F Gb Ab Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(63),  // Eb
            Note.fromMidi(65),  // F
            Note.fromMidi(66),  // Gb
            Note.fromMidi(68),  // Ab
            Note.fromMidi(70)   // Bb
        );
    }

    @Test
    void shouldGenerateAlteredScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("ALTERED", c4);
        // C Altered: C Db Eb Fb Gb Bbb Bb
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // Db
            Note.fromMidi(63),  // Eb
            Note.fromMidi(64),  // Fb
            Note.fromMidi(66),  // Gb
            Note.fromMidi(68),  // Bbb (enharmonic Ab)
            Note.fromMidi(70)   // Bb
        );
    }

    // Escalas pentatónicas (Pentatonic Scales)
    @Test
    void shouldGeneratePentatonicMajorScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("PENTATONIC_MAJOR", c4);
        // C Major Pentatonic: C D E G A (5 notas)
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(67),  // G
            Note.fromMidi(69)   // A
        );
    }

    // Escalas simétricas (Symmetric Scales)
    @Test
    void shouldGenerateChromaticScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("CHROMATIC", c4);
        // C Chromatic: all 12 semitones
        assertThat(scale.getNotes()).hasSize(12);
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // C#
            Note.fromMidi(62),  // D
            Note.fromMidi(63),  // Eb
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(66),  // F#
            Note.fromMidi(67),  // G
            Note.fromMidi(68),  // Ab
            Note.fromMidi(69),  // A
            Note.fromMidi(70),  // Bb
            Note.fromMidi(71)   // B
        );
    }

    @Test
    void shouldGenerateWholeToneScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("WHOLE_TONE", c4);
        // C Whole Tone: C D E F# G# A# (6 notas, cada uma a 2 semítons)
        assertThat(scale.getNotes()).hasSize(6);
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(66),  // F#
            Note.fromMidi(68),  // G#
            Note.fromMidi(70)   // A#
        );
    }

    @Test
    void shouldGenerateHalfWoleOctatonicScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("HALF_WHOLE_OCTATONIC", c4);
        // C Half-Whole Octatonic: C Db Eb E F# G A Bb (8 notas)
        assertThat(scale.getNotes()).hasSize(8);
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // Db
            Note.fromMidi(63),  // Eb
            Note.fromMidi(64),  // E
            Note.fromMidi(66),  // F#
            Note.fromMidi(67),  // G
            Note.fromMidi(69),  // A
            Note.fromMidi(70)   // Bb
        );
    }

    @Test
    void shouldGenerateWholeHalfOctatonicScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("WHOLE_HALF_OCTATONIC", c4);
        // C Whole-Half Octatonic: C D E F# G Ab Bb B (8 notas)
        assertThat(scale.getNotes()).hasSize(8);
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(63),  // Eb (enharmonic D#)
            Note.fromMidi(65),  // F
            Note.fromMidi(66),  // F# (enharmonic Gb)
            Note.fromMidi(68),  // Ab
            Note.fromMidi(69),  // A (enharmonic Bbb)
            Note.fromMidi(71)   // B
        );
    }

    // Escalas harmónicas (Harmonic Scales)
    @Test
    void shouldGenerateHarmonicMajorScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("HARMONIC_MAJOR", c4);
        // C Harmonic Major: C D E F G Ab B
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(62),  // D
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(68),  // Ab
            Note.fromMidi(71)   // B
        );
    }

    @Test
    void shouldGenerateDoubleHarmonicMajorScale() {
        Note c4 = Note.fromMidi(60);
        Scale scale = Scale.get("DOUBLE_HARMONIC_MAJOR", c4);
        // C Double Harmonic Major: C Db E F G Ab B
        assertThat(scale.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(61),  // Db
            Note.fromMidi(64),  // E
            Note.fromMidi(65),  // F
            Note.fromMidi(67),  // G
            Note.fromMidi(68),  // Ab
            Note.fromMidi(71)   // B
        );
    }

    // Aliases - usam os mesmos intervalos, só testamos que o nome funciona
    @Test
    void shouldHandleIonianAsAlias() {
        Note c4 = Note.fromMidi(60);
        Scale ionian = Scale.get("IONIAN", c4);
        Scale major = Scale.get("MAJOR", c4);
        assertThat(ionian.getNotes()).isEqualTo(major.getNotes());
    }

    @Test
    void shouldHandleAeolianAsAlias() {
        Note c4 = Note.fromMidi(60);
        Scale aeolian = Scale.get("AEOLIAN", c4);
        Scale naturalMinor = Scale.get("MINOR_NATURAL", c4);
        assertThat(aeolian.getNotes()).isEqualTo(naturalMinor.getNotes());
    }

    @Test
    void shouldHandleSuperLocrianAsAlias() {
        Note c4 = Note.fromMidi(60);
        Scale superLocrian = Scale.get("SUPER_LOCRIAN", c4);
        Scale altered = Scale.get("ALTERED", c4);
        assertThat(superLocrian.getNotes()).isEqualTo(altered.getNotes());
    }

    @Test
    void shouldHandleMinorBluesAsAlias() {
        Note c4 = Note.fromMidi(60);
        Scale minorBlues = Scale.get("MINOR_BLUES", c4);
        Scale blues = Scale.get("BLUES", c4);
        assertThat(minorBlues.getNotes()).isEqualTo(blues.getNotes());
    }

    @Test
    void shouldHandleByzantineAsAlias() {
        Note c4 = Note.fromMidi(60);
        Scale byzantine = Scale.get("BYZANTINE", c4);
        Scale doubleHarmonic = Scale.get("DOUBLE_HARMONIC_MAJOR", c4);
        assertThat(byzantine.getNotes()).isEqualTo(doubleHarmonic.getNotes());
    }
}
