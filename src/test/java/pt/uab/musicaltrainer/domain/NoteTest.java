package pt.uab.musicaltrainer.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes do modelo de domínio Note para representação de notas MIDI.
 * <p>
 * Valida conversão de números MIDI (0-127) para nomes de notas,
 * oitavas, e formatação de display (ex: C4, G4).
 *
 * @author Daniel Junior
 */
class NoteTest {

    @Test
    void shouldConvertMidiNumberToNote() {
        Note note = Note.fromMidi(60);
        assertThat(note.getName()).isEqualTo("C");
        assertThat(note.getOctave()).isEqualTo(4);
    }

    @Test
    void shouldConvertMidiNumberG4() {
        Note note = Note.fromMidi(67);
        assertThat(note.getName()).isEqualTo("G");
        assertThat(note.getOctave()).isEqualTo(4);
    }

    @Test
    void shouldConvertMidiNumberC3() {
        Note note = Note.fromMidi(48);
        assertThat(note.getName()).isEqualTo("C");
        assertThat(note.getOctave()).isEqualTo(3);
    }

    @Test
    void shouldReturnMidiNumber() {
        Note note = Note.fromMidi(60);
        assertThat(note.getMidiNumber()).isEqualTo(60);
    }

    @Test
    void shouldReturnCorrectDisplayName() {
        Note note = Note.fromMidi(60);
        assertThat(note.getDisplayName()).isEqualTo("C4");
    }

    @Test
    void shouldHandleAllChromatic() {
        Note c = Note.fromMidi(60);
        Note cSharp = Note.fromMidi(61);
        Note d = Note.fromMidi(62);
        Note dSharp = Note.fromMidi(63);
        Note e = Note.fromMidi(64);
        Note f = Note.fromMidi(65);
        Note fSharp = Note.fromMidi(66);
        Note g = Note.fromMidi(67);

        assertThat(c.getName()).isEqualTo("C");
        assertThat(cSharp.getName()).isEqualTo("C#");
        assertThat(d.getName()).isEqualTo("D");
        assertThat(dSharp.getName()).isEqualTo("D#");
        assertThat(e.getName()).isEqualTo("E");
        assertThat(f.getName()).isEqualTo("F");
        assertThat(fSharp.getName()).isEqualTo("F#");
        assertThat(g.getName()).isEqualTo("G");
    }

    @Test
    void shouldThrowExceptionForInvalidMidiNumber() {
        assertThatThrownBy(() -> Note.fromMidi(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MIDI number must be between 0 and 127");

        assertThatThrownBy(() -> Note.fromMidi(128))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MIDI number must be between 0 and 127");
    }

    @Test
    void getPitchClassNameShouldReturnNameWithoutOctave() {
        assertThat(Note.fromMidi(60).getPitchClassName()).isEqualTo("C");
        assertThat(Note.fromMidi(61).getPitchClassName()).isEqualTo("C#");
        assertThat(Note.fromMidi(69).getPitchClassName()).isEqualTo("A");
        assertThat(Note.fromMidi(70).getPitchClassName()).isEqualTo("A#");
    }

    @Test
    void getPitchClassNameShouldNotContainOctaveDigit() {
        for (int midi = 36; midi <= 84; midi++) {
            String name = Note.fromMidi(midi).getPitchClassName();
            assertThat(name).as("getPitchClassName() de MIDI %d nao deve conter digito", midi)
                .doesNotMatch(".*\\d.*");
        }
    }
}
