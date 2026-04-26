package pt.uab.musicaltrainer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação de um acorde musical.
 * <p>
 * Value object imutável definido por dois componentes: tipo de acorde (MAJOR, MINOR, etc.)
 * e nota raiz. Dois acordes com o mesmo tipo e raiz são semanticamente equivalentes.
 * <p>
 * <b>Comparação e igualdade:</b> ChordImpl implementa equals() e hashCode() baseados em type e root.
 * Isto permite comparação confiável entre acordes e uso em coleções hash-based (Set, HashMap).
 * As notas são derivadas dos componentes (type + root), logo não influenciam igualdade.
 * <p>
 * <b>Uso em persistência:</b> Atualmente, acordes geram exercícios cujas respostas são serializadas
 * como strings (nota por nota). No futuro, metadados de acorde (tipo, raiz) podem ser armazenados
 * em resultados para análise de padrões de erro. A igualdade confiável permite comparação segura.
 * <p>
 * Atualmente suporta tríades (3 notas): MAJOR, MINOR, DIMINISHED, AUGMENTED.
 * Futuras expansões: inversões, acordes estendidos (C7, Cmaj7, etc).
 * Ver OI09 em docs/scope/requirements.md.
 *
 * @author Daniel Junior
 */
final class ChordImpl implements Chord, NoteGenerator {

    private final String type;
    private final Note root;
    private final List<Note> notes;

    private ChordImpl(String type, Note root, List<Note> notes) {
        this.type = type;
        this.root = root;
        this.notes = List.copyOf(notes);
    }

    /**
     * Cria um acorde a partir de uma nota raiz e tipo.
     *
     * @param chordType tipo de acorde
     * @param root nota raiz do acorde
     * @return acorde gerado
     */
    static Chord get(String chordType, Note root) {
        int[] intervals = getIntervalPattern(chordType);
        ChordImpl chordImpl = new ChordImpl(chordType, root, new ArrayList<>());
        List<Note> notes = chordImpl.generateNotes(root, intervals);
        return new ChordImpl(chordType, root, notes);
    }

    /**
     * Retorna o padrão de semítons para cada tipo de acorde.
     * Os valores são as distâncias em semítons a partir da nota raiz.
     */
    private static int[] getIntervalPattern(String chordType) {
        return ChordType.valueOf(chordType).getIntervals();
    }


    /**
     * Retorna o tipo do acorde.
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Retorna a nota raiz do acorde.
     */
    @Override
    public Note getRoot() {
        return root;
    }

    /**
     * Retorna a lista de notas deste acorde.
     */
    @Override
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * Compara acordes por value object: igualdade quando type e root coincidem.
     * <p>
     * Notas NÃO influenciam igualdade; são derivadas determinísticamente de (type + root).
     * Assim, Chord.get("MAJOR", C4).equals(Chord.get("MAJOR", C4)) == true,
     * mesmo que sejam instâncias diferentes.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChordImpl chord = (ChordImpl) o;
        return type.equals(chord.type) && root.equals(chord.root);
    }

    /**
     * Hash code baseado em type e root, consistente com equals().
     * Permite uso seguro em Set<Chord> e como chave em Map<Chord, V>.
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, root);
    }

    @Override
    public String toString() {
        return getType() + " chord starting from " + getRoot().getName();
    }
}
