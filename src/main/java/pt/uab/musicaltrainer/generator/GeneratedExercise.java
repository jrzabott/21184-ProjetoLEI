package pt.uab.musicaltrainer.generator;

import java.util.List;

/**
 * Resultado imutável de um gerador de exercícios.
 * Contém dados para guardar em BD e dados para enriquecer a resposta REST.
 */
public record GeneratedExercise(
    String type,
    int difficulty,
    String questionJson,
    String correctAnswer,
    String description,
    String hint,
    int[] notesToPlay,
    List<String> options
) {}
