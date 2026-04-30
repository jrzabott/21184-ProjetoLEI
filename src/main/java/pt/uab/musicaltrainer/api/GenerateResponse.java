package pt.uab.musicaltrainer.api;

import java.util.List;

public record GenerateResponse(
    Long exerciseId,
    String type,
    int difficulty,
    int suggestedDifficulty,
    int[] notes,
    String description,
    String hint,
    List<String> options
) {}
