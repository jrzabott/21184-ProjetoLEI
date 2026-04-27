package pt.uab.musicaltrainer.api;

import java.time.LocalDateTime;

public record SessionStartResponse(Long sessionId, LocalDateTime startedAt) {}
