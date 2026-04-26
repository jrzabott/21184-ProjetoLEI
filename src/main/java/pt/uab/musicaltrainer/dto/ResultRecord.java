package pt.uab.musicaltrainer.dto;

import java.time.LocalDateTime;

/**
 * Result record - immutable DTO for exercise result data.
 * Represents a user's response to an exercise and whether it was correct.
 */
public record ResultRecord(
    Long id,
    Long sessionId,
    Long exerciseId,
    String userAnswer,
    Boolean isCorrect,
    LocalDateTime createdAt
) {}
