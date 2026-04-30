package pt.uab.musicaltrainer.api;

import java.util.List;

public record GenerateResponse(
    Long exerciseId,
    String type,
    int difficulty,
    int[] notes,
    String description,
    List<String> options
) {}
