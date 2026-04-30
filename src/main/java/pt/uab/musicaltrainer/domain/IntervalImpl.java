package pt.uab.musicaltrainer.domain;

/**
 * Implementação de um intervalo musical entre duas notas.
 * <p>
 * Value object imutável. Nome e semítons são delegados ao enum
 * IntervalType - sem duplicação de dados.
 *
 * @author Daniel Junior
 */
final class IntervalImpl implements Interval {

    private final int semitones;
    private final String name;

    private IntervalImpl(int semitones, String name) {
        this.semitones = semitones;
        this.name = name;
    }

    static Interval between(Note low, Note high) {
        int distance = Math.abs(high.getMidiNumber() - low.getMidiNumber());
        IntervalType type = IntervalType.fromSemitones(distance);
        return new IntervalImpl(type.semitones(), type.internalName());
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getSemitones() { return semitones; }

    @Override
    public String toString() {
        return name + " (" + semitones + " semítone" + (semitones == 1 ? "" : "s") + ")";
    }
}
