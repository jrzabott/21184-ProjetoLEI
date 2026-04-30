package pt.uab.musicaltrainer.generator;

/**
 * Contrato para geradores de exercícios de teoria musical.
 * Cada implementação gera um tipo específico de exercício.
 * Geradores são stateless — cada chamada é independente.
 */
public interface ExerciseGenerator {

    /**
     * Gera um novo exercício aleatório para o nível indicado.
     *
     * @param difficulty nível 1-10 (1 mais fácil, 10 mais difícil)
     * @return exercício gerado com todos os dados necessários
     */
    GeneratedExercise generate(int difficulty);

    /**
     * Reconstrói os dados de exibição a partir da questão guardada em BD.
     * Necessário porque a BD guarda apenas o mínimo (schema ADR-013).
     *
     * @param questionJson JSON guardado no campo question da BD
     * @param correctAnswer resposta correcta guardada na BD
     * @param difficulty nível do exercício
     * @return dados de exibição reconstruídos (description, notes, options)
     */
    GeneratedExercise fromStored(String questionJson, String correctAnswer, int difficulty);

    /**
     * Tipo de exercício que este gerador produz.
     */
    String getExerciseType();
}
