package pt.uab.musicaltrainer.api;

public record SessionEndResponse(
    Long sessionId,
    int totalExercises,
    int correctAnswers,
    double accuracy,
    long durationSeconds
) {}
