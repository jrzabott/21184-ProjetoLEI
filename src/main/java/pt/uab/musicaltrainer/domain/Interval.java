package pt.uab.musicaltrainer.domain;

/**
 * Representa um intervalo musical entre duas notas.
 * Imutável. Identifica o intervalo por semítons e nome completo (ex: 5ª Perfeita).
 *
 * @author Daniel Junior
 */
public final class Interval {

    private static final String[] INTERVAL_NAMES = {
        "Uníssono",
        "2ª Menor",
        "2ª Maior",
        "3ª Menor",
        "3ª Maior",
        "4ª Perfeita",
        "4ª Aumentada / 5ª Diminuta",
        "5ª Perfeita",
        "5ª Aumentada / 6ª Menor",
        "6ª Maior",
        "6ª Aumentada / 7ª Menor",
        "7ª Maior",
        "Oitava Perfeita"
    };

    private final int semitones;
    private final String name;

    private Interval(int semitones, String name) {
        this.semitones = semitones;
        this.name = name;
    }

    /**
     * Cria um intervalo entre duas notas.
     *
     * @param low primeira nota (nota mais baixa ou referência)
     * @param high segunda nota (nota mais alta ou destino)
     * @return intervalo entre as duas notas
     */
    public static Interval between(Note low, Note high) {
        int semitoneDifference = Math.abs(high.getMidiNumber() - low.getMidiNumber());

        int intervalIndex = semitoneDifference % 13;
        if (intervalIndex >= INTERVAL_NAMES.length) {
            intervalIndex = semitoneDifference % 12;
        }

        String intervalName = INTERVAL_NAMES[intervalIndex];
        return new Interval(semitoneDifference, intervalName);
    }

    /**
     * Retorna o nome completo do intervalo (ex: "5ª Perfeita").
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna o número de semítons entre as notas.
     */
    public int getSemitones() {
        return semitones;
    }

    @Override
    public String toString() {
        return name + " (" + semitones + " semítone" + (semitones == 1 ? "" : "s") + ")";
    }
}
