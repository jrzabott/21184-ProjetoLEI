package pt.uab.musicaltrainer.domain;

/**
 * Contrato para uma nota musical representada pelo seu número MIDI.
 * <p>
 * Suporta conversão bidirecional entre números MIDI (0-127) e
 * representações legíveis (nome + oitava, ex: C4, G4).
 * <p>
 * Value object imutável: duas notas com o mesmo número MIDI
 * são semanticamente equivalentes.
 *
 * @author Daniel Junior
 */
public interface Note {

    /**
     * Retorna a instância em cache da nota correspondente ao número MIDI.
     * <p>
     * Este método é muito eficiente — não cria novas instâncias.
     * Duas chamadas com o mesmo número MIDI retornam exactamente
     * a mesma instância (igualdade de referência).
     *
     * @param midiNumber número MIDI entre 0 e 127
     * @return instância em cache da nota
     * @throws IllegalArgumentException se midiNumber estiver fora do intervalo válido
     */
    static Note fromMidi(int midiNumber) {
        return NoteImpl.fromMidi(midiNumber);
    }

    /**
     * Retorna o nome da nota (ex: C, C#, D, etc.).
     */
    String getName();

    /**
     * Retorna a oitava da nota (ex: 3, 4, 5).
     */
    int getOctave();

    /**
     * Retorna o número MIDI original.
     */
    int getMidiNumber();

    /**
     * Retorna a representação legível da nota (ex: C4, G#5).
     */
    String getDisplayName();
}
