package pt.uab.musicaltrainer.domain;

/**
 * Tipos de escalas musicais com padrões de semítons.
 * <p>
 * Enum que define todos os tipos de escalas suportados e seus padrões de intervalos.
 * Os valores são as distâncias em semítons a partir da nota raiz.
 * <p>
 * Organizado por famílias:
 * - Escalas maiores diatónicas (Jónica/Maior)
 * - Escalas menores diatónicas (Aeoliana/Natural, Harmónica, Melódica)
 * - Modos derivados da escala diatónica (Dórico, Frígio, Lídio, Mixolídio, Lócrio)
 * - Variantes menores e alteradas
 * - Escalas pentatónicas e blues
 * - Escalas simétricas
 * - Escalas harmónicas
 *
 * @author Daniel Junior
 */
public enum ScaleType {
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

    /**
     * Retorna o padrão de semítons para este tipo de escala.
     */
    public int[] getIntervals() {
        return intervals;
    }

    /**
     * Retorna os tamanhos dos passos entre notas consecutivas (raiz→raiz oitava acima).
     * Usado para validar respostas de exercícios de forma independente de oitava.
     * <p>
     * Exemplo MAJOR: [2, 2, 1, 2, 2, 2, 1] (W W H W W W H)
     * Tem sempre intervals.length elementos (um por passo, incluindo o regresso à raiz).
     */
    public int[] getSemitonePattern() {
        int[] pattern = new int[intervals.length];
        for (int i = 1; i < intervals.length; i++) {
            pattern[i - 1] = intervals[i] - intervals[i - 1];
        }
        // último passo: de volta à raiz (oitava)
        pattern[intervals.length - 1] = 12 - intervals[intervals.length - 1];
        return pattern;
    }
}
