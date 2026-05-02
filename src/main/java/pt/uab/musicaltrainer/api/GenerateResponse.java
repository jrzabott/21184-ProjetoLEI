package pt.uab.musicaltrainer.api;

public record GenerateResponse(
    Long exerciseId,
    String type,
    int difficulty,
    int suggestedDifficulty,
    int[] notes,
    String description,
    String hint
) {}
