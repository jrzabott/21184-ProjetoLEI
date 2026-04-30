package pt.uab.musicaltrainer.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementação de uma escala musical.
 * <p>
 * Value object imutável definido por dois componentes: tipo de escala (MAJOR, MINOR_NATURAL, etc.)
 * e nota raiz. Duas escalas com o mesmo tipo e raiz são semanticamente equivalentes.
 * <p>
 * <b>Comparação e igualdade:</b> ScaleImpl implementa equals() e hashCode() baseados em type e root.
 * Isto permite comparação confiável entre escalas e uso em coleções hash-based (Set, HashMap).
 * As notas são derivadas dos componentes (type + root), logo não influenciam igualdade.
 * <p>
 * <b>Uso em persistência:</b> Atualmente, escalas geram exercícios cujas respostas são serializadas
 * como strings (nota por nota). No futuro, metadados de escala (tipo, raiz) podem ser armazenados
 * em resultados para análise de padrões de erro. A igualdade confiável permite comparação segura.
 * <p>
 * Suporta 25+ tipos de escalas: modos diatónicos, menores alteradas, simétricas, harmónicas,
 * pentatónicas. Aliases (ex: IONIAN=MAJOR) partilham os mesmos intervalos via constructor overloading.
 *
 * @author Daniel Junior
 */
final class ScaleImpl implements Scale, NoteGenerator {

    private final String type;
    private final Note root;
    private final List<Note> notes;

    private ScaleImpl(String type, Note root, List<Note> notes) {
        this.type = type;
        this.root = root;
        this.notes = List.copyOf(notes);
    }

    /**
     * Cria uma escala a partir de uma nota raiz e tipo.
     *
     * @param scaleType tipo de escala
     * @param root nota raiz da escala
     * @return escala gerada
     */
    static Scale get(String scaleType, Note root) {
        int[] intervals = getIntervalPattern(scaleType);
        ScaleImpl scaleImpl = new ScaleImpl(scaleType, root, new ArrayList<>());
        List<Note> notes = scaleImpl.generateNotes(root, intervals);
        return new ScaleImpl(scaleType, root, notes);
    }

    /**
     * Retorna o padrão de semítons para cada tipo de escala.
     * Os valores são as distâncias em semítons a partir da nota raiz.
     */
    private static int[] getIntervalPattern(String scaleType) {
        return ScaleType.valueOf(scaleType).getIntervals();
    }


    /**
     * Retorna o tipo da escala.
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Retorna a nota raiz da escala.
     */
    @Override
    public Note getRoot() {
        return root;
    }

    /**
     * Retorna a lista de notas desta escala.
     */
    @Override
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * Compara escalas por value object: igualdade quando type e root coincidem.
     * <p>
     * Notas NÃO influenciam igualdade; são derivadas determinísticamente de (type + root).
     * Assim, Scale.get("MAJOR", C4).equals(Scale.get("MAJOR", C4)) == true,
     * mesmo que sejam instâncias diferentes.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScaleImpl scale = (ScaleImpl) o;
        return type.equals(scale.type) && root.equals(scale.root);
    }

    /**
     * Hash code baseado em type e root, consistente com equals().
     * Permite uso seguro em Set<Scale> e como chave em Map<Scale, V>.
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, root);
    }

    @Override
    public String toString() {
        return getType() + " scale starting from " + getRoot().getName();
    }
}
