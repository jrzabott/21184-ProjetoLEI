package pt.uab.musicaltrainer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes para a classe Chord (modelo de domínio).
 * <p>
 * Valida a geração correcta de acordes musicais a partir de uma nota raiz
 * e tipo de acorde (Maior, Menor, Diminuto, Aumentado).
 *
 * @author Daniel Junior
 */
class ChordTest {

    /**
     * Testa acorde Maior (C Major) - C E G.
     * C=60, E=64 (major 3rd = +4), G=67 (perfect 5th = +7)
     */
    @Test
    void shouldGenerateCMajorChord() {
        Note c4 = Note.fromMidi(60);
        Chord chord = Chord.get("MAJOR", c4);

        assertThat(chord.getType()).isEqualTo("MAJOR");
        assertThat(chord.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(64),  // E
            Note.fromMidi(67)   // G
        );
    }

    /**
     * Testa acorde Menor (A Minor) - A C E.
     * A=57, C=60 (minor 3rd = +3), E=64 (perfect 5th = +7)
     */
    @Test
    void shouldGenerateAMinorChord() {
        Note a3 = Note.fromMidi(57);
        Chord chord = Chord.get("MINOR", a3);

        assertThat(chord.getType()).isEqualTo("MINOR");
        assertThat(chord.getNotes()).containsExactly(
            Note.fromMidi(57),  // A
            Note.fromMidi(60),  // C
            Note.fromMidi(64)   // E
        );
    }

    /**
     * Testa acorde Diminuto (B Diminished) - B D F.
     * B=71, D=74 (minor 3rd = +3), F=77 (diminished 5th = +6)
     */
    @Test
    void shouldGenerateBDiminishedChord() {
        Note b4 = Note.fromMidi(71);
        Chord chord = Chord.get("DIMINISHED", b4);

        assertThat(chord.getType()).isEqualTo("DIMINISHED");
        assertThat(chord.getNotes()).containsExactly(
            Note.fromMidi(71),  // B
            Note.fromMidi(74),  // D
            Note.fromMidi(77)   // F
        );
    }

    /**
     * Testa acorde Aumentado (C Augmented) - C E G#.
     * C=60, E=64 (major 3rd = +4), G#=68 (augmented 5th = +8)
     */
    @Test
    void shouldGenerateCaugmentedChord() {
        Note c4 = Note.fromMidi(60);
        Chord chord = Chord.get("AUGMENTED", c4);

        assertThat(chord.getType()).isEqualTo("AUGMENTED");
        assertThat(chord.getNotes()).containsExactly(
            Note.fromMidi(60),  // C
            Note.fromMidi(64),  // E
            Note.fromMidi(68)   // G#
        );
    }

    /**
     * Testa que tipos diferentes produzem notas diferentes.
     */
    @Test
    void shouldDifferentiateBetweenChordTypes() {
        Note c4 = Note.fromMidi(60);
        Chord major = Chord.get("MAJOR", c4);
        Chord minor = Chord.get("MINOR", c4);

        assertThat(major.getNotes()).isNotEqualTo(minor.getNotes());
    }

    /**
     * Testa que acordes gerados a partir de raízes diferentes têm notas diferentes.
     */
    @Test
    void shouldDifferentiateBetweenRoots() {
        Note c4 = Note.fromMidi(60);
        Note d4 = Note.fromMidi(62);
        Chord cMajor = Chord.get("MAJOR", c4);
        Chord dMajor = Chord.get("MAJOR", d4);

        assertThat(cMajor.getNotes()).isNotEqualTo(dMajor.getNotes());
    }

    // Value object equality tests
    @Test
    void shouldBeEqualWhenTypeAndRootMatch() {
        Note c4 = Note.fromMidi(60);
        Chord major1 = Chord.get("MAJOR", c4);
        Chord major2 = Chord.get("MAJOR", c4);
        assertThat(major1).isEqualTo(major2);
    }

    @Test
    void shouldNotBeEqualWhenTypesDiffer() {
        Note c4 = Note.fromMidi(60);
        Chord major = Chord.get("MAJOR", c4);
        Chord minor = Chord.get("MINOR", c4);
        assertThat(major).isNotEqualTo(minor);
    }

    @Test
    void shouldNotBeEqualWhenRootsDiffer() {
        Note c4 = Note.fromMidi(60);
        Note d4 = Note.fromMidi(62);
        Chord cMajor = Chord.get("MAJOR", c4);
        Chord dMajor = Chord.get("MAJOR", d4);
        assertThat(cMajor).isNotEqualTo(dMajor);
    }

    @Test
    void shouldHaveSameHashCodeWhenEqual() {
        Note c4 = Note.fromMidi(60);
        Chord major1 = Chord.get("MAJOR", c4);
        Chord major2 = Chord.get("MAJOR", c4);
        assertThat(major1.hashCode()).isEqualTo(major2.hashCode());
    }

    @Test
    void shouldBeUsableInHashBasedCollections() {
        Note c4 = Note.fromMidi(60);
        Note d4 = Note.fromMidi(62);
        Chord cMajor = Chord.get("MAJOR", c4);
        Chord dMajor = Chord.get("MAJOR", d4);

        java.util.Set<Chord> chordSet = new java.util.HashSet<>();
        chordSet.add(cMajor);
        chordSet.add(dMajor);
        chordSet.add(cMajor);  // duplicate

        assertThat(chordSet).hasSize(2);  // duplicate not added
        assertThat(chordSet).contains(cMajor, dMajor);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Note c4 = Note.fromMidi(60);
        Chord major = Chord.get("MAJOR", c4);
        assertThat(major).isNotEqualTo(null);
    }

    @Test
    void shouldNotBeEqualToDifferentType() {
        Note c4 = Note.fromMidi(60);
        Chord major = Chord.get("MAJOR", c4);
        assertThat(major).isNotEqualTo("MAJOR");
        assertThat(major).isNotEqualTo(c4);
    }
}
