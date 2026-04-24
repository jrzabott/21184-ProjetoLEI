package pt.uab.musicaltrainer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma escala musical.
 * Imutável. Gerada a partir de uma nota raiz e tipo de escala (Maior, Menor, etc.).
 *
 * @author Daniel Junior
 */
public final class Scale {

    private final String type;
    private final Note root;
    private final List<Note> notes;

    private Scale(String type, Note root, List<Note> notes) {
        this.type = type;
        this.root = root;
        this.notes = List.copyOf(notes);
    }

    /**
     * Cria uma escala a partir de uma nota raiz e tipo.
     *
     * @param scaleType tipo de escala (MAJOR, MINOR_NATURAL, HARMONIC_MINOR, DORIAN, PENTATONIC_MINOR, BLUES)
     * @param root nota raiz da escala
     * @return escala gerada
     */
    public static Scale get(String scaleType, Note root) {
        int[] intervals = getIntervalPattern(scaleType);
        List<Note> notes = generateNotes(root, intervals);
        return new Scale(scaleType, root, notes);
    }

    /**
     * Retorna o padrão de semítons para cada tipo de escala.
     * Os valores são as distâncias em semítons a partir da nota raiz.
     */
    private static int[] getIntervalPattern(String scaleType) {
        return switch (scaleType) {
            case "MAJOR" -> new int[]{0, 2, 4, 5, 7, 9, 11};
            case "MINOR_NATURAL" -> new int[]{0, 2, 3, 5, 7, 8, 10};
            case "HARMONIC_MINOR" -> new int[]{0, 2, 3, 5, 7, 8, 11};
            case "DORIAN" -> new int[]{0, 2, 3, 5, 7, 9, 10};
            case "PENTATONIC_MINOR" -> new int[]{0, 3, 5, 7, 10};
            case "BLUES" -> new int[]{0, 3, 5, 6, 7, 10};
            default -> throw new IllegalArgumentException("Tipo de escala desconhecido: " + scaleType);
        };
    }

    /**
     * Gera as notas da escala aplicando os intervalos à nota raiz.
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
     * Retorna o tipo da escala.
     */
    public String getType() {
        return type;
    }

    /**
     * Retorna a nota raiz da escala.
     */
    public Note getRoot() {
        return root;
    }

    /**
     * Retorna a lista de notas desta escala.
     */
    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return type + " scale starting from " + root.getName();
    }
}
