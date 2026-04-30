package pt.uab.musicaltrainer.domain;

/**
 * Contrato para uma nota musical representada pelo seu número MIDI.
 * <p>
 * Uma nota musical é definida por altura (pitch) — a sua posição numa escala,
 * e é representada internamente por um número MIDI (0-127), onde cada número
 * corresponde a um semitom. Exemplo: C4 (Dó na 4ª oitava) = 60 MIDI,
 * G4 (Sol na 4ª oitava) = 67 MIDI. A cada semitom aumentado, o número MIDI
 * aumenta em 1, subindo um semitom na escala cromática.
 * <p>
 * Suporta conversão bidirecional entre números MIDI (0-127) e
 * representações legíveis (nome + oitava, ex: C4, G4, D#5).
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
