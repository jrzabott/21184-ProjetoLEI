package pt.uab.musicaltrainer.domain;

import java.util.List;

/**
 * Contrato para uma escala musical.
 * <p>
 * Uma escala é um conjunto ordenado de notas derivadas de uma nota raiz
 * e tipo de escala (MAJOR, MINOR_NATURAL, HARMONIC_MINOR, etc).
 * <p>
 * Value object imutável: duas escalas com o mesmo tipo e raiz são equivalentes.
 *
 * @author Daniel Junior
 */
public interface Scale {

    /**
     * Cria uma escala a partir de uma nota raiz e tipo.
     *
     * @param scaleType tipo de escala (ver ScaleType enum)
     * @param root nota raiz da escala
     * @return escala gerada
     * @throws IllegalArgumentException se scaleType inválido
     */
    static Scale get(String scaleType, Note root) {
        return ScaleImpl.get(scaleType, root);
    }

    /**
     * Retorna o tipo da escala.
     */
    String getType();

    /**
     * Retorna a nota raiz da escala.
     */
    Note getRoot();

    /**
     * Retorna a lista imutável de notas desta escala.
     */
    List<Note> getNotes();
}
