package pt.uab.musicaltrainer.dto;

import java.time.LocalDateTime;

/**
 * Session record - immutable DTO for session data.
 * Represents a training session with metadata.
 */
public record SessionRecord(
    Long id,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer totalExercises,
    Integer correctAnswers,
    Integer incorrectAnswers,
    LocalDateTime createdAt
) {}
