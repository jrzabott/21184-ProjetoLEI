package pt.uab.musicaltrainer.api;

public record AnswerResponse(boolean correct, String correctAnswer, String userAnswer, String explanation) {}
