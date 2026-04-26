package pt.uab.musicaltrainer.dto;

import java.time.LocalDateTime;

/**
 * Exercise record - immutable DTO for exercise data.
 * Represents a single generated exercise instance.
 */
public record ExerciseRecord(
    Long id,
    String type,
    Integer difficulty,
    String question,
    String correctAnswer,
    LocalDateTime createdAt
) {}
