package pt.uab.musicaltrainer.domain;

/**
 * Contrato para um intervalo musical entre duas notas.
 * <p>
 * Um intervalo é a distância em semítons entre duas notas,
 * com nome completo (ex: 5ª Perfeita).
 * <p>
 * Value object imutável: dois intervalos com a mesma distância em semítons
 * são semanticamente equivalentes.
 *
 * @author Daniel Junior
 */
public interface Interval {

    /**
     * Cria um intervalo entre duas notas.
     *
     * @param low primeira nota (nota mais baixa ou referência)
     * @param high segunda nota (nota mais alta ou destino)
     * @return intervalo entre as duas notas
     */
    static Interval between(Note low, Note high) {
        return IntervalImpl.between(low, high);
    }

    /**
     * Retorna o nome completo do intervalo (ex: "5ª Perfeita").
     */
    String getName();

    /**
     * Retorna o número de semítons entre as notas.
     */
    int getSemitones();
}
