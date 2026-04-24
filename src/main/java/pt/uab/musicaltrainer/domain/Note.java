package pt.uab.musicaltrainer.domain;

/**
 * Representa uma nota musical através do seu número MIDI.
 * <p>
 * Suporta conversão bidirecional entre números MIDI (0-127) e
 * representações legíveis (nome + oitava, ex: C4, G4).
 * <p>
 * Esta classe é imutável (value object). Duas notas com o mesmo
 * número MIDI são consideradas iguais.
 *
 * @author Daniel Junior
 */
public final class Note {
    private static final String[] NOTES = {
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };

    private final int midiNumber;
    private final String name;
    private final int octave;

    /**
     * Cria uma nota a partir de um número MIDI.
     * <p>
     * Valida que o número MIDI está entre 0 e 127 (inclusive).
     * A oitava é calculada automaticamente: (MIDI / 12) - 1.
     *
     * @param midiNumber número MIDI (0-127)
     * @return nova instância de Note
     * @throws IllegalArgumentException se MIDI está fora do intervalo válido
     */
    public static Note fromMidi(int midiNumber) {
        return new Note(midiNumber);
    }

    private Note(int midiNumber) {
        if (midiNumber < 0 || midiNumber > 127) {
            throw new IllegalArgumentException("MIDI number must be between 0 and 127");
        }
        this.midiNumber = midiNumber;
        this.octave = (midiNumber / 12) - 1;
        this.name = NOTES[midiNumber % 12];
    }

    /**
     * Retorna o nome da nota (ex: C, C#, D, etc.).
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna a oitava da nota (ex: 3, 4, 5).
     */
    public int getOctave() {
        return octave;
    }

    /**
     * Retorna o número MIDI original.
     */
    public int getMidiNumber() {
        return midiNumber;
    }

    /**
     * Retorna a representação legível da nota (ex: C4, G#5).
     */
    public String getDisplayName() {
        return name + octave;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return midiNumber == note.midiNumber;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(midiNumber);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
