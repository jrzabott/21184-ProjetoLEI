package pt.uab.musicaltrainer.domain;

/**
 * Níveis de dificuldade semânticos para exercícios de teoria musical.
 * <p>
 * Encapsula os valores numéricos 1-10 em bandas com significado pedagógico.
 * Elimina magic numbers nos geradores de exercícios. Cada nível diz não só
 * "é difícil" mas "é difícil PORQUE..." - as notas, intervalos e conceitos
 * aumentam de complexidade musical à medida que o estudante aprende.
 * <p>
 * Classificação:
 * - BEGINNER (1-2):     Escala maior, intervalos consonantes (P1, M3, P5, P8)
 * - ELEMENTARY (3-4):   Menor natural e pentatónicas; M2, m3, P4, m7, M7
 * - INTERMEDIATE (5-6): Menor harmónica e blues; dissonâncias moderadas
 * - ADVANCED (7-8):     Modos diatónicos, escala de tons; intervalos alterados
 * - EXPERT (9-10):      Escalas exóticas e cromática; todos os intervalos
 * <p>
 * Referência: ADR-015 - Classificação de Dificuldade
 *
 * @author Daniel Junior
 */
public enum DifficultyLevel {

    /** 1-2 - Material mais familiar: escala maior, intervalos consonantes. */
    BEGINNER(1, 2),

    /** 3-4 - Segunda camada: menor natural, pentatónicas; intervalos comuns. */
    ELEMENTARY(3, 4),

    /** 5-6 - Intermédio: menor harmónica, blues; dissonâncias moderadas. */
    INTERMEDIATE(5, 6),

    /** 7-8 - Avançado: modos, escala de tons; intervalos alterados. */
    ADVANCED(7, 8),

    /** 9-10 - Especialista: escalas exóticas, cromática; todos os intervalos. */
    EXPERT(9, 10);

    private final int min;
    private final int max;

    DifficultyLevel(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int lowerBound() { return min; }
    public int upperBound() { return max; }

    /**
     * Converte valor numérico 1-10 para nível semântico.
     * Valores fora do intervalo são clampados.
     */
    public static DifficultyLevel of(int difficulty) {
        int clamped = Math.max(1, Math.min(10, difficulty));
        return values()[(clamped - 1) / 2];
    }
}
