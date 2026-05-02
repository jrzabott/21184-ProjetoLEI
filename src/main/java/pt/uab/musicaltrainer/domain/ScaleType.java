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
    MAJOR(new int[]{0, 2, 4, 5, 7, 9, 11}, DifficultyLevel.BEGINNER,    "Maior"),
    IONIAN(MAJOR),

    // Família das escalas menores diatónicas
    MINOR_NATURAL(new int[]{0, 2, 3, 5, 7, 8, 10}, DifficultyLevel.ELEMENTARY,   "Menor Natural"),
    AEOLIAN(MINOR_NATURAL),
    HARMONIC_MINOR(new int[]{0, 2, 3, 5, 7, 8, 11}, DifficultyLevel.INTERMEDIATE, "Menor Harmónica"),
    MELODIC_MINOR(new int[]{0, 2, 3, 5, 7, 9, 11},  DifficultyLevel.INTERMEDIATE, "Menor Melódica"),

    // Modos derivados da escala diatónica
    DORIAN(new int[]{0, 2, 3, 5, 7, 9, 10},    DifficultyLevel.ADVANCED, "Dórico"),
    PHRYGIAN(new int[]{0, 1, 3, 5, 7, 8, 10},  DifficultyLevel.ADVANCED, "Frígio"),
    LYDIAN(new int[]{0, 2, 4, 6, 7, 9, 11},    DifficultyLevel.ADVANCED, "Lídio"),
    MIXOLYDIAN(new int[]{0, 2, 4, 5, 7, 9, 10},DifficultyLevel.ADVANCED, "Mixolídio"),
    LOCRIAN(new int[]{0, 1, 3, 5, 6, 8, 10},   DifficultyLevel.ADVANCED, "Lócrio"),

    // Variantes menores e cores dominantes alteradas
    PHRYGIAN_DOMINANT(new int[]{0, 1, 4, 5, 7, 8, 10}, DifficultyLevel.EXPERT, "Frígio Dominante"),
    LYDIAN_DOMINANT(new int[]{0, 2, 4, 6, 7, 9, 10},   DifficultyLevel.EXPERT, "Lídio Dominante"),
    DORIAN_FLAT_2(new int[]{0, 1, 3, 5, 7, 9, 10},     DifficultyLevel.EXPERT, "Dórico b2"),
    LOCRIAN_NATURAL_2(new int[]{0, 2, 3, 5, 6, 8, 10}, DifficultyLevel.EXPERT, "Lócrio Natural 2"),
    ALTERED(new int[]{0, 1, 3, 4, 6, 8, 10},           DifficultyLevel.EXPERT, "Alterada"),
    SUPER_LOCRIAN(ALTERED),

    // Escalas pentatónicas e blues
    PENTATONIC_MAJOR(new int[]{0, 2, 4, 7, 9},    DifficultyLevel.ELEMENTARY,  "Pentatónica Maior"),
    PENTATONIC_MINOR(new int[]{0, 3, 5, 7, 10},   DifficultyLevel.ELEMENTARY,  "Pentatónica Menor"),
    BLUES(new int[]{0, 3, 5, 6, 7, 10},           DifficultyLevel.INTERMEDIATE, "Blues"),
    MINOR_BLUES(BLUES),

    // Escalas simétricas
    CHROMATIC(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, DifficultyLevel.EXPERT, "Cromática"),
    WHOLE_TONE(new int[]{0, 2, 4, 6, 8, 10},               DifficultyLevel.ADVANCED, "Tons Inteiros"),
    HALF_WHOLE_OCTATONIC(new int[]{0, 1, 3, 4, 6, 7, 9, 10},  DifficultyLevel.EXPERT, "Octatónica S-T"),
    WHOLE_HALF_OCTATONIC(new int[]{0, 2, 3, 5, 6, 8, 9, 11},  DifficultyLevel.EXPERT, "Octatónica T-S"),

    // Família da escala maior harmónica
    HARMONIC_MAJOR(new int[]{0, 2, 4, 5, 7, 8, 11},      DifficultyLevel.ADVANCED, "Maior Harmónica"),
    DOUBLE_HARMONIC_MAJOR(new int[]{0, 1, 4, 5, 7, 8, 11}, DifficultyLevel.EXPERT, "Maior Dupla Harmónica"),
    BYZANTINE(DOUBLE_HARMONIC_MAJOR),
    ;

    private final int[] intervals;
    private final DifficultyLevel difficulty;
    private final String displayName;
    private final boolean alias;

    ScaleType(int[] intervals, DifficultyLevel difficulty, String displayName) {
        this.intervals   = intervals;
        this.difficulty  = difficulty;
        this.displayName = displayName;
        this.alias = false;
    }

    ScaleType(ScaleType aliasOf) {
        this.intervals   = aliasOf.intervals;
        this.difficulty  = aliasOf.difficulty;
        this.displayName = aliasOf.displayName;
        this.alias = true;
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
     * Retorna o nome legível em português para este tipo de escala.
     */
    public String displayName() { return displayName; }

    /** Devolve true se este tipo e um alias de outro (ex: IONIAN e alias de MAJOR). */
    public boolean isAlias() { return alias; }

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
