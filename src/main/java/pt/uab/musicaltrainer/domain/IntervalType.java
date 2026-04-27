package pt.uab.musicaltrainer.domain;

/**
 * Tipos de intervalos musicais com nome e distância em semítons.
 * <p>
 * Enum que define os 13 intervalos diatónicos (0-12 semítons).
 * Segue o padrão de ScaleType e ChordType — fonte única de verdade
 * para nomes de intervalos em todo o projecto.
 * <p>
 * Intervalos enarmónicos (TRITONO, QUINTA_AUM) usam nome composto.
 * Ver OI10 em docs/scope/requirements.md para separação futura.
 *
 * @author Daniel Junior
 */
public enum IntervalType {

    /** 0 semítons — nota repetida. */
    UNISSONO         ("Uníssono",                     0),

    /** 1 semítom — ex: C4 → C#4. */
    SEGUNDA_MENOR    ("2ª Menor",                     1),

    /** 2 semítons — ex: C4 → D4. */
    SEGUNDA_MAIOR    ("2ª Maior",                     2),

    /** 3 semítons — ex: C4 → Eb4. */
    TERCA_MENOR      ("3ª Menor",                     3),

    /** 4 semítons — ex: C4 → E4. */
    TERCA_MAIOR      ("3ª Maior",                     4),

    /** 5 semítons — ex: C4 → F4. */
    QUARTA_PERFEITA  ("4ª Perfeita",                  5),

    /** 6 semítons — tritono; enarmónico de 4ª Aumentada e 5ª Diminuta. Ver OI10. */
    TRITONO          ("4ª Aumentada / 5ª Diminuta",   6),

    /** 7 semítons — ex: C4 → G4. */
    QUINTA_PERFEITA  ("5ª Perfeita",                  7),

    /** 8 semítons — enarmónico de 5ª Aumentada e 6ª Menor. Ver OI10. */
    QUINTA_AUM       ("5ª Aumentada / 6ª Menor",      8),

    /** 9 semítons — ex: C4 → A4. */
    SEXTA_MAIOR      ("6ª Maior",                     9),

    /** 10 semítons — enarmónico de 6ª Aumentada e 7ª Menor. */
    SETIMA_MENOR     ("6ª Aumentada / 7ª Menor",     10),

    /** 11 semítons — ex: C4 → B4. */
    SETIMA_MAIOR     ("7ª Maior",                    11),

    /** 12 semítons — oitava. Ex: C4 → C5. */
    OITAVA_PERFEITA  ("Oitava Perfeita",             12);

    private final String displayName;
    private final int semitones;

    IntervalType(String displayName, int semitones) {
        this.displayName = displayName;
        this.semitones = semitones;
    }

    public String displayName() { return displayName; }
    public int semitones()      { return semitones; }

    /**
     * Devolve o tipo de intervalo para a distância em semítons indicada.
     * Para distâncias superiores a 12, aplica módulo 13 (wrap para simples).
     */
    public static IntervalType fromSemitones(int semitones) {
        return values()[semitones % 13];
    }
}
