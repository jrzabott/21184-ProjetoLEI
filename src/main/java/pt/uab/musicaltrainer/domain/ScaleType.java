package pt.uab.musicaltrainer.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    MAJOR(new int[]{0, 2, 4, 5, 7, 9, 11}, DifficultyLevel.BEGINNER),
    IONIAN(MAJOR),

    // Família das escalas menores diatónicas
    MINOR_NATURAL(new int[]{0, 2, 3, 5, 7, 8, 10}, DifficultyLevel.ELEMENTARY),
    AEOLIAN(MINOR_NATURAL),
    HARMONIC_MINOR(new int[]{0, 2, 3, 5, 7, 8, 11}, DifficultyLevel.INTERMEDIATE),
    MELODIC_MINOR(new int[]{0, 2, 3, 5, 7, 9, 11}, DifficultyLevel.INTERMEDIATE),

    // Modos derivados da escala diatónica
    DORIAN(new int[]{0, 2, 3, 5, 7, 9, 10}, DifficultyLevel.ADVANCED),
    PHRYGIAN(new int[]{0, 1, 3, 5, 7, 8, 10}, DifficultyLevel.ADVANCED),
    LYDIAN(new int[]{0, 2, 4, 6, 7, 9, 11}, DifficultyLevel.ADVANCED),
    MIXOLYDIAN(new int[]{0, 2, 4, 5, 7, 9, 10}, DifficultyLevel.ADVANCED),
    LOCRIAN(new int[]{0, 1, 3, 5, 6, 8, 10}, DifficultyLevel.ADVANCED),

    // Variantes menores e cores dominantes alteradas
    PHRYGIAN_DOMINANT(new int[]{0, 1, 4, 5, 7, 8, 10}, DifficultyLevel.EXPERT),
    LYDIAN_DOMINANT(new int[]{0, 2, 4, 6, 7, 9, 10}, DifficultyLevel.EXPERT),
    DORIAN_FLAT_2(new int[]{0, 1, 3, 5, 7, 9, 10}, DifficultyLevel.EXPERT),
    LOCRIAN_NATURAL_2(new int[]{0, 2, 3, 5, 6, 8, 10}, DifficultyLevel.EXPERT),
    ALTERED(new int[]{0, 1, 3, 4, 6, 8, 10}, DifficultyLevel.EXPERT),
    SUPER_LOCRIAN(ALTERED),

    // Escalas pentatónicas e blues
    PENTATONIC_MAJOR(new int[]{0, 2, 4, 7, 9}, DifficultyLevel.ELEMENTARY),
    PENTATONIC_MINOR(new int[]{0, 3, 5, 7, 10}, DifficultyLevel.ELEMENTARY),
    BLUES(new int[]{0, 3, 5, 6, 7, 10}, DifficultyLevel.INTERMEDIATE),
    MINOR_BLUES(BLUES),

    // Escalas simétricas
    CHROMATIC(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, DifficultyLevel.EXPERT),
    WHOLE_TONE(new int[]{0, 2, 4, 6, 8, 10}, DifficultyLevel.ADVANCED),
    HALF_WHOLE_OCTATONIC(new int[]{0, 1, 3, 4, 6, 7, 9, 10}, DifficultyLevel.EXPERT),
    WHOLE_HALF_OCTATONIC(new int[]{0, 2, 3, 5, 6, 8, 9, 11}, DifficultyLevel.EXPERT),

    // Família da escala maior harmónica
    HARMONIC_MAJOR(new int[]{0, 2, 4, 5, 7, 8, 11}, DifficultyLevel.ADVANCED),
    DOUBLE_HARMONIC_MAJOR(new int[]{0, 1, 4, 5, 7, 8, 11}, DifficultyLevel.EXPERT),
    BYZANTINE(DOUBLE_HARMONIC_MAJOR),
    ;

    private final int[] intervals;
    private final DifficultyLevel difficulty;

    ScaleType(int[] intervals, DifficultyLevel difficulty) {
        this.intervals  = intervals;
        this.difficulty = difficulty;
    }

    ScaleType(ScaleType aliasOf) {
        this.intervals  = aliasOf.intervals;
        this.difficulty = aliasOf.difficulty;
    }

    /**
     * Retorna o padrão de semítons para este tipo de escala.
     */
    public int[] getIntervals() {
        return intervals;
    }

    /**
     * Retorna o nível de dificuldade desta escala.
     */
    public DifficultyLevel difficulty() {
        return difficulty;
    }

    /**
     * Retorna todas as escalas disponíveis até ao nível de dificuldade indicado (inclusive).
     *
     * @param band nivel de dificuldade máximo
     * @return lista de tipos de escala com dificuldade <= band
     */
    public static List<ScaleType> availableFor(DifficultyLevel band) {
        return Arrays.stream(values())
            .filter(t -> t.difficulty.ordinal() <= band.ordinal())
            .collect(Collectors.toList());
    }

}
