package pt.uab.musicaltrainer.api;

/**
 * Resposta de avaliação de exercício.
 * correctAnswer e userAnswer são arrays de números MIDI directos,
 * consistentes com o campo notes em GenerateResponse (ADR-014).
 * Correcção do bug P29: anteriormente eram strings contendo JSON.
 */
public record AnswerResponse(
    boolean correct,
    int[]   correctAnswer,
    int[]   userAnswer,
    String  explanation
) {}
