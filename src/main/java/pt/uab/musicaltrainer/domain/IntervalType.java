package pt.uab.musicaltrainer.domain;

/**
 * Tipos de intervalos musicais.
 * <p>
 * Cada valor tem dois nomes:
 * - internalName(): ASCII seguro, usado em BD, logica backend, getName()
 * - displayName(): PT-PT completo com caracteres especiais, apenas para frontend
 * <p>
 * Regra de projecto: caracteres especiais (a com ordinal, acentos) apenas em displayName().
 *
 * @author Daniel Junior
 */
public enum IntervalType {

    UNISSONO         ("Unissono",                          "Uníssono",                    0),
    SEGUNDA_MENOR    ("2a Menor",                          "2ª Menor",                    1),
    SEGUNDA_MAIOR    ("2a Maior",                          "2ª Maior",                    2),
    TERCA_MENOR      ("3a Menor",                          "3ª Menor",                    3),
    TERCA_MAIOR      ("3a Maior",                          "3ª Maior",                    4),
    QUARTA_PERFEITA  ("4a Perfeita",                       "4ª Perfeita",                 5),
    TRITONO          ("4a Aumentada / 5a Diminuta",        "4ª Aumentada / 5ª Diminuta",  6),
    QUINTA_PERFEITA  ("5a Perfeita",                       "5ª Perfeita",                 7),
    QUINTA_AUM       ("5a Aumentada / 6a Menor",           "5ª Aumentada / 6ª Menor",     8),
    SEXTA_MAIOR      ("6a Maior",                          "6ª Maior",                    9),
    SETIMA_MENOR     ("6a Aumentada / 7a Menor",           "6ª Aumentada / 7ª Menor",    10),
    SETIMA_MAIOR     ("7a Maior",                          "7ª Maior",                   11),
    OITAVA_PERFEITA  ("Oitava Perfeita",                   "Oitava Perfeita",            12);

    private final String internalName;
    private final String displayName;
    private final int semitones;

    IntervalType(String internalName, String displayName, int semitones) {
        this.internalName = internalName;
        this.displayName = displayName;
        this.semitones = semitones;
    }

    /** Nome ASCII seguro - para BD, logica backend, API de dados. */
    public String internalName() { return internalName; }

    /** Nome PT-PT com caracteres especiais - apenas para apresentacao frontend. */
    public String displayName()  { return displayName; }

    public int semitones()       { return semitones; }

    public static IntervalType fromSemitones(int semitones) {
        return values()[semitones % 13];
    }
}
