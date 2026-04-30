package pt.uab.musicaltrainer.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tipos de acordes musicais com padrões de semítons.
 * <p>
 * Enum que define todos os tipos de acordes suportados (atualmente tríades)
 * e seus padrões de intervalos. Os valores são as distâncias em semítons
 * a partir da nota raiz.
 * <p>
 * Suportados:
 * - MAJOR: raiz + terça maior (4 semítons) + quinta perfeita (7 semítons)
 * - MINOR: raiz + terça menor (3 semítons) + quinta perfeita (7 semítons)
 * - DIMINISHED: raiz + terça menor (3 semítons) + quinta diminuta (6 semítons)
 * - AUGMENTED: raiz + terça maior (4 semítons) + quinta aumentada (8 semítons)
 * <p>
 * Futuras expansões: inversões (1ª, 2ª), acordes estendidos (7, maj7, etc).
 * Ver OI09 em docs/scope/requirements.md para plano de evolução.
 *
 * @author Daniel Junior
 */
public enum ChordType {
    /**
     * Acorde Maior: raiz, terça maior, quinta perfeita.
     * Intervalos: +0, +4, +7 semítons.
     */
    MAJOR(new int[]{0, 4, 7}, DifficultyLevel.BEGINNER,      "Maior"),

    /**
     * Acorde Menor: raiz, terça menor, quinta perfeita.
     * Intervalos: +0, +3, +7 semítons.
     */
    MINOR(new int[]{0, 3, 7}, DifficultyLevel.INTERMEDIATE,  "Menor"),

    /**
     * Acorde Diminuto: raiz, terça menor, quinta diminuta.
     * Intervalos: +0, +3, +6 semítons.
     */
    DIMINISHED(new int[]{0, 3, 6}, DifficultyLevel.ADVANCED,      "Diminuto"),

    /**
     * Acorde Aumentado: raiz, terça maior, quinta aumentada.
     * Intervalos: +0, +4, +8 semítons.
     */
    AUGMENTED(new int[]{0, 4, 8}, DifficultyLevel.ADVANCED,      "Aumentado");

    private final int[] intervals;
    private final DifficultyLevel difficulty;
    private final String displayName;

    ChordType(int[] intervals, DifficultyLevel difficulty, String displayName) {
        this.intervals   = intervals;
        this.difficulty  = difficulty;
        this.displayName = displayName;
    }

    /**
     * Retorna o padrão de semítons para este tipo de acorde.
     */
    public int[] getIntervals() {
        return intervals;
    }

    /**
     * Retorna o nível de dificuldade deste acorde.
     */
    public DifficultyLevel difficulty() {
        return difficulty;
    }

    /**
     * Retorna o nome legível em português para este tipo de acorde.
     */
    public String displayName() { return displayName; }

    /**
     * Retorna todos os acordes disponíveis até ao nível de dificuldade indicado (inclusive).
     *
     * @param band nivel de dificuldade máximo
     * @return lista de tipos de acorde com dificuldade <= band
     */
    public static List<ChordType> availableFor(DifficultyLevel band) {
        return Arrays.stream(values())
            .filter(t -> t.difficulty.ordinal() <= band.ordinal())
            .collect(Collectors.toList());
    }

    /**
     * Retorna os intervalos entre notas consecutivas em voicing I-III-V.
     * Usado para validar respostas independente de oitava (ADR-014).
     * <p>
     * Exemplo MAJOR [0,4,7]: diferenças = [4, 3] (terça maior + terça menor)
     */
    public int[] getVoicingIntervals() {
        return new int[]{intervals[1] - intervals[0], intervals[2] - intervals[1]};
    }
}
