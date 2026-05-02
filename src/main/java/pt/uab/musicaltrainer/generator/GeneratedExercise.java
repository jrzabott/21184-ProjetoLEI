package pt.uab.musicaltrainer.generator;

/**
 * Resultado imutavel de um gerador de exercicios.
 * ADR-014: sem campo options — protocolo baseado em notas MIDI, sem multipla escolha.
 */
public record GeneratedExercise(
    String type,
    int difficulty,
    String questionJson,
    String correctAnswer,
    String description,
    String hint,
    int[] notesToPlay
) {}
