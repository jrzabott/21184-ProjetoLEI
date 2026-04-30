package pt.uab.musicaltrainer.domain;

/**
 * Contrato para um intervalo musical entre duas notas.
 * <p>
 * Um intervalo é a distância entre duas notas, medida em semítons (unidade mínima
 * da escala cromática). Cada intervalo tem um nome que descreve a sua função musical.
 * Exemplo: C4 para G4 = 7 semítons = 5ª Perfeita (consonante, som "completo").
 * Um intervalo de 4 semítons (C4 para E4) é uma 3ª Maior (também consonante).
 * Os intervalos são a base para construir acordes, escalas e harmonia em geral.
 * <p>
 * Qualidade musical:
 * - Consonantes (soam bem): Uníssono, 3ª, 4ª, 5ª, 6ª, oitava
 * - Dissonantes (pedem resolução): 2ª, 7ª, trítono
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

    String getName();

    int getSemitones();
}
