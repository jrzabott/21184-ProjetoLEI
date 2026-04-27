package pt.uab.musicaltrainer.api;

public record AnswerRequest(Long sessionId, String answer, long responseTimeMs) {}
