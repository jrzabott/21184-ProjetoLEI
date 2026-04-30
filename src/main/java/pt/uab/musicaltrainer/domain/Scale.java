package pt.uab.musicaltrainer.domain;

import java.util.List;

/**
 * Contrato para uma escala musical.
 * <p>
 * Uma escala é um conjunto ordenado de notas derivadas de uma nota raiz,
 * seguindo uma fórmula de intervalos que define o seu tipo (MAJOR, MINOR_NATURAL,
 * HARMONIC_MINOR, etc). A escala é o "esqueleto" de uma composição — define
 * que notas soam bem juntas.
 * <p>
 * Exemplo - Escala Maior (C Major): Fórmula W-W-H-W-W-W-H (W = tom, H = semitom)
 * C → D (tom) → E (tom) → F (semitom) → G (tom) → A (tom) → B (tom) → C (semitom)
 * = 8 notas (sete + a repetição da raiz uma oitava acima).
 * <p>
 * A escala Maior é a mais comum — é "brilhante", "feliz". A escala Menor Natural
 * é mais "escura", "triste". Outras escalas (Harmónica, Melódica, Modos, Blues, Pentatónica)
 * oferecem cores diferentes e são essenciais em vários géneros musicais.
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
