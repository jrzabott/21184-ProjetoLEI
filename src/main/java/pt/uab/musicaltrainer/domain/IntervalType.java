package pt.uab.musicaltrainer.domain;

/**
 * Tipos de intervalos musicais.
 * <p>
 * Cada valor tem dois nomes e um nível de dificuldade:
 * - internalName(): ASCII seguro, usado em BD, lógica backend, getName()
 * - displayName(): PT-PT completo com caracteres especiais, apenas para frontend
 * - difficulty(): nível pedagógico para selecção por dificuldade
 * <p>
 * Regra de projecto: caracteres especiais (ordinal ª, acentos) apenas em displayName().
 * Referência: ADR-014 (protocolo notas MIDI), ADR-015 (taxonomia dificuldade)
 *
 * @author Daniel Junior
 */
public enum IntervalType {

    UNISSONO        ("Unissono",                   "Uníssono",                   0,  DifficultyLevel.BEGINNER),
    SEGUNDA_MENOR   ("2a Menor",                   "2ª Menor",                   1,  DifficultyLevel.ADVANCED),
    SEGUNDA_MAIOR   ("2a Maior",                   "2ª Maior",                   2,  DifficultyLevel.ELEMENTARY),
    TERCA_MENOR     ("3a Menor",                   "3ª Menor",                   3,  DifficultyLevel.ELEMENTARY),
    TERCA_MAIOR     ("3a Maior",                   "3ª Maior",                   4,  DifficultyLevel.BEGINNER),
    QUARTA_PERFEITA ("4a Perfeita",                "4ª Perfeita",                5,  DifficultyLevel.ELEMENTARY),
    TRITONO         ("4a Aumentada / 5a Diminuta", "4ª Aumentada / 5ª Diminuta", 6,  DifficultyLevel.ADVANCED),
    QUINTA_PERFEITA ("5a Perfeita",                "5ª Perfeita",                7,  DifficultyLevel.BEGINNER),
    QUINTA_AUM      ("5a Aumentada / 6a Menor",    "5ª Aumentada / 6ª Menor",    8,  DifficultyLevel.ADVANCED),
    SEXTA_MAIOR     ("6a Maior",                   "6ª Maior",                   9,  DifficultyLevel.ADVANCED),
    SETIMA_MENOR    ("6a Aumentada / 7a Menor",    "6ª Aumentada / 7ª Menor",   10,  DifficultyLevel.ELEMENTARY),
    SETIMA_MAIOR    ("7a Maior",                   "7ª Maior",                  11,  DifficultyLevel.ELEMENTARY),
    OITAVA_PERFEITA ("Oitava Perfeita",            "Oitava Perfeita",           12,  DifficultyLevel.BEGINNER);

    private final String internalName;
    private final String displayName;
    private final int semitones;
    private final DifficultyLevel difficulty;

    IntervalType(String internalName, String displayName, int semitones, DifficultyLevel difficulty) {
        this.internalName = internalName;
        this.displayName  = displayName;
        this.semitones    = semitones;
        this.difficulty   = difficulty;
    }

    public String internalName()       { return internalName; }
    public String displayName()        { return displayName; }
    public int semitones()             { return semitones; }
    public DifficultyLevel difficulty(){ return difficulty; }

    /**
     * Devolve todos os intervalos disponíveis até ao nível indicado (inclusive).
     * Usado pelos geradores para seleccionar dentro da dificuldade pedida.
     */
    public static java.util.List<IntervalType> availableFor(DifficultyLevel band) {
        return java.util.Arrays.stream(values())
            .filter(t -> t.difficulty.ordinal() <= band.ordinal())
            .collect(java.util.stream.Collectors.toList());
    }

    /** Devolve o tipo de intervalo para a distância em semítons (wrap com % 13). */
    public static IntervalType fromSemitones(int semitones) {
        return values()[semitones % 13];
    }
}
