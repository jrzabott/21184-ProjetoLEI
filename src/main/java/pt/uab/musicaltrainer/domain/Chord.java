package pt.uab.musicaltrainer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um acorde musical.
 * <p>
 * Value object imutável definido por dois componentes: tipo de acorde (MAJOR, MINOR, etc.)
 * e nota raiz. Dois acordes com o mesmo tipo e raiz são semanticamente equivalentes.
 * <p>
 * Atualmente suporta tríades (3 notas): MAJOR, MINOR, DIMINISHED, AUGMENTED.
 * Geradas a partir de intervalos determinísticos em semítons.
 * <p>
 * <b>Comparação e igualdade:</b> Chord implementa equals() e hashCode() baseados em type e root.
 * As notas são derivadas dos componentes, logo não influenciam igualdade.
 * Permite uso em coleções hash-based (Set, HashMap).
 * <p>
 * <b>Uso em persistência:</b> Acordes geram exercícios cujas respostas são serializadas
 * como strings (nota por nota). No futuro, metadados de acorde (tipo, raiz) podem estar
 * em resultados para análise de padrões de erro.
 *
 * @author Daniel Junior
 */
public final class Chord {

    private final String type;
    private final Note root;
    private final List<Note> notes;

    private Chord(String type, Note root, List<Note> notes) {
        this.type = type;
        this.root = root;
        this.notes = List.copyOf(notes);
    }

    /**
     * Cria um acorde a partir de uma nota raiz e tipo.
     *
     * @param chordType tipo de acorde (MAJOR, MINOR, DIMINISHED, AUGMENTED)
     * @param root nota raiz do acorde
     * @return acorde gerado
     */
    public static Chord get(String chordType, Note root) {
        int[] intervals = getIntervalPattern(chordType);
        List<Note> notes = generateNotes(root, intervals);
        return new Chord(chordType, root, notes);
    }

    /**
     * Retorna o padrão de semítons para cada tipo de acorde.
     * Os valores são as distâncias em semítons a partir da nota raiz.
     */
    private static int[] getIntervalPattern(String chordType) {
        return switch (chordType) {
            case "MAJOR" -> new int[]{0, 4, 7};           // raiz, major 3rd, perfect 5th
            case "MINOR" -> new int[]{0, 3, 7};           // raiz, minor 3rd, perfect 5th
            case "DIMINISHED" -> new int[]{0, 3, 6};      // raiz, minor 3rd, diminished 5th
            case "AUGMENTED" -> new int[]{0, 4, 8};       // raiz, major 3rd, augmented 5th
            default -> throw new IllegalArgumentException("Tipo de acorde desconhecido: " + chordType);
        };
    }

    /**
     * Gera as notas do acorde aplicando os intervalos à nota raiz.
     */
    private static List<Note> generateNotes(Note root, int[] intervals) {
        List<Note> result = new ArrayList<>();
        int rootMidi = root.getMidiNumber();

        for (int interval : intervals) {
            int noteMidi = rootMidi + interval;
            result.add(Note.fromMidi(noteMidi));
        }

        return result;
    }

    /**
     * Retorna o tipo do acorde.
     */
    public String getType() {
        return type;
    }

    /**
     * Retorna a nota raiz do acorde.
     */
    public Note getRoot() {
        return root;
    }

    /**
     * Retorna a lista de notas deste acorde.
     */
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
        Chord chord = (Chord) o;
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
