package pt.uab.musicaltrainer.domain;

import java.util.Objects;

/**
 * Implementação de uma nota musical através do seu número MIDI.
 * <p>
 * Suporta conversão bidirecional entre números MIDI (0-127) e
 * representações legíveis (nome + oitava, ex: C4, G4).
 * <p>
 * Esta classe é imutável (value object). Todas as 128 instâncias possíveis
 * estão pré-calculadas e em cache. Duas chamadas com o mesmo número MIDI
 * retornam exactamente a mesma instância (igualdade de referência).
 *
 * @author Daniel Junior
 */
final class NoteImpl implements Note {
    private static final String[] NOTES = {
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };

    // Cache de todas as 128 notas MIDI possíveis
    private static final NoteImpl[] CACHE = new NoteImpl[128];

    static {
        for (int i = 0; i < 128; i++) {
            CACHE[i] = new NoteImpl(i);
        }
    }

    private final int midiNumber;
    private final String name;
    private final int octave;

    /**
     * Retorna a instância em cache da nota correspondente ao número MIDI.
     * <p>
     * Este método é muito eficiente - não cria novas instâncias.
     * Duas chamadas com o mesmo número MIDI retornam exactamente
     * a mesma instância (igualdade de referência).
     *
     * @param midiNumber número MIDI entre 0 e 127
     * @return instância em cache da nota
     * @throws IllegalArgumentException se midiNumber estiver fora do intervalo válido
     */
    static Note fromMidi(int midiNumber) {
        if (midiNumber < 0 || midiNumber > 127) {
            throw new IllegalArgumentException("MIDI number must be between 0 and 127");
        }
        return CACHE[midiNumber];
    }

    private NoteImpl(int midiNumber) {
        this.midiNumber = midiNumber;
        this.octave = (midiNumber / 12) - 1;
        this.name = NOTES[midiNumber % 12];
    }

    /**
     * Retorna o nome da nota (ex: C, C#, D, etc.).
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Retorna a oitava da nota (ex: 3, 4, 5).
     */
    @Override
    public int getOctave() {
        return octave;
    }

    /**
     * Retorna o número MIDI original.
     */
    @Override
    public int getMidiNumber() {
        return midiNumber;
    }

    /**
     * Retorna a representação legível da nota (ex: C4, G#5).
     */
    @Override
    public String getDisplayName() {
        return name + octave;
    }

    /**
     * Retorna o pitch class sem oitava (ex: C#, A).
     * Usar em enunciados onde a oitava nao deve restringir o utilizador.
     */
    @Override
    public String getPitchClassName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteImpl note = (NoteImpl) o;
        return midiNumber == note.midiNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(midiNumber);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
