package pt.uab.musicaltrainer.domain;

import java.util.List;

/**
 * Contrato para um acorde musical.
 * <p>
 * Um acorde é um conjunto de 3 ou mais notas tocadas simultaneamente (ou em arpejo).
 * O acorde mais simples é a tríade, que consiste em: raiz + 3ª + 5ª.
 * Exemplo - Acorde C Major: C (raiz) + E (3ª Maior, 4 semítons) + G (5ª Perfeita, 7 semítons).
 * <p>
 * Os tipos de acordes definem qualidade harmónica:
 * - MAJOR (raiz + M3 + P5): som "brilhante", positivo
 * - MINOR (raiz + m3 + P5): som "escuro", melancólico
 * - DIMINISHED (raiz + m3 + d5): som "tenso", dissonante
 * - AUGMENTED (raiz + M3 + A5): som "ambíguo", suspenso
 * <p>
 * Acordes são blocos de construção da harmonia e progressões de acordes
 * (sequências de acordes) definem o caráter emocional e estrutura de uma composição.
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
