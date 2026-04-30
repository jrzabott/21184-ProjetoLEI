package pt.uab.musicaltrainer.api;

import java.util.List;
import java.util.Map;

public record ProgressResponse(
    long totalSessions,
    long totalExercises,
    double overallAccuracy,
    Map<String, TypeStats> byType,
    List<SessionSummary> recentSessions
) {
    public record TypeStats(double accuracy, long totalAnswers) {}
    public record SessionSummary(Long sessionId, double accuracy, int totalExercises) {}
}
