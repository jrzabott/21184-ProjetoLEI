package pt.uab.musicaltrainer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma escala musical.
 * <p>
 * Value object imutável definido por dois componentes: tipo de escala (MAJOR, MINOR_NATURAL, etc.)
 * e nota raiz. Duas escalas com o mesmo tipo e raiz são semanticamente equivalentes.
 * <p>
 * <b>Comparação e igualdade:</b> Scale implementa equals() e hashCode() baseados em type e root.
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
     * @param scaleType tipo de escala
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
        return ScaleType.valueOf(scaleType).getIntervals();
    }

    private enum ScaleType {
        // Família das escalas maiores diatónicas
        MAJOR(new int[]{0, 2, 4, 5, 7, 9, 11}),
        IONIAN(MAJOR),

        // Família das escalas menores diatónicas
        MINOR_NATURAL(new int[]{0, 2, 3, 5, 7, 8, 10}),
        AEOLIAN(MINOR_NATURAL),
        HARMONIC_MINOR(new int[]{0, 2, 3, 5, 7, 8, 11}),
        MELODIC_MINOR(new int[]{0, 2, 3, 5, 7, 9, 11}),

        // Modos derivados da escala diatónica
        DORIAN(new int[]{0, 2, 3, 5, 7, 9, 10}),
        PHRYGIAN(new int[]{0, 1, 3, 5, 7, 8, 10}),
        LYDIAN(new int[]{0, 2, 4, 6, 7, 9, 11}),
        MIXOLYDIAN(new int[]{0, 2, 4, 5, 7, 9, 10}),
        LOCRIAN(new int[]{0, 1, 3, 5, 6, 8, 10}),

        // Variantes menores e cores dominantes alteradas
        PHRYGIAN_DOMINANT(new int[]{0, 1, 4, 5, 7, 8, 10}),
        LYDIAN_DOMINANT(new int[]{0, 2, 4, 6, 7, 9, 10}),
        DORIAN_FLAT_2(new int[]{0, 1, 3, 5, 7, 9, 10}),
        LOCRIAN_NATURAL_2(new int[]{0, 2, 3, 5, 6, 8, 10}),
        ALTERED(new int[]{0, 1, 3, 4, 6, 8, 10}),
        SUPER_LOCRIAN(ALTERED),

        // Escalas pentatónicas e blues
        PENTATONIC_MAJOR(new int[]{0, 2, 4, 7, 9}),
        PENTATONIC_MINOR(new int[]{0, 3, 5, 7, 10}),
        BLUES(new int[]{0, 3, 5, 6, 7, 10}),
        MINOR_BLUES(BLUES),

        // Escalas simétricas
        CHROMATIC(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}),
        WHOLE_TONE(new int[]{0, 2, 4, 6, 8, 10}),
        HALF_WHOLE_OCTATONIC(new int[]{0, 1, 3, 4, 6, 7, 9, 10}),
        WHOLE_HALF_OCTATONIC(new int[]{0, 2, 3, 5, 6, 8, 9, 11}),

        // Família da escala maior harmónica
        HARMONIC_MAJOR(new int[]{0, 2, 4, 5, 7, 8, 11}),
        DOUBLE_HARMONIC_MAJOR(new int[]{0, 1, 4, 5, 7, 8, 11}),
        BYZANTINE(DOUBLE_HARMONIC_MAJOR),
        ;

        private final int[] intervals;

        ScaleType(int[] intervals) {
            this.intervals = intervals;
        }

        ScaleType(ScaleType aliasOf) {
            this.intervals = aliasOf.intervals;
        }

        public int[] getIntervals() {
            return intervals;
        }
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
        Scale scale = (Scale) o;
        return type.equals(scale.type) && root.equals(scale.root);
    }

    /**
     * Hash code baseado em type e root, consistente com equals().
     * Permite uso seguro em Set<Scale> e como chave em Map<Scale, V>.
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, root);
    }

    @Override
    public String toString() {
        return getType() + " scale starting from " + getRoot().getName();
    }
}
