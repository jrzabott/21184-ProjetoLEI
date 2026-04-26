package pt.uab.musicaltrainer.domain;

import java.util.List;

/**
 * Contrato para um acorde musical.
 * <p>
 * Um acorde é um conjunto de notas derivadas de uma nota raiz
 * e tipo de acorde (MAJOR, MINOR, DIMINISHED, AUGMENTED).
 * <p>
 * Value object imutável: dois acordes com o mesmo tipo e raiz são equivalentes.
 *
 * @author Daniel Junior
 */
public interface Chord {

    /**
     * Cria um acorde a partir de uma nota raiz e tipo.
     *
     * @param chordType tipo de acorde (ver ChordType enum)
     * @param root nota raiz do acorde
     * @return acorde gerado
     * @throws IllegalArgumentException se chordType inválido
     */
    static Chord get(String chordType, Note root) {
        return ChordImpl.get(chordType, root);
    }

    /**
     * Retorna o tipo do acorde.
     */
    String getType();

    /**
     * Retorna a nota raiz do acorde.
     */
    Note getRoot();

    /**
     * Retorna a lista imutável de notas deste acorde.
     */
    List<Note> getNotes();
}
