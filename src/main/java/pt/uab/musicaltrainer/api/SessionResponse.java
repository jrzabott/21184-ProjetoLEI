package pt.uab.musicaltrainer.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Response unificado para start e end de sessão.
 * Campos endedAt, accuracy, durationSeconds são nulos na resposta de start
 * e omitidos do JSON por @JsonInclude(NON_NULL).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionResponse(
    Long sessionId,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    Integer totalExercises,
    Integer correctAnswers,
    Integer incorrectAnswers,
    Double accuracy,
    Long durationSeconds
) {}
