package pt.uab.musicaltrainer.api;

import java.util.List;
import java.util.Map;

public record ProgressResponse(
    long totalSessions,
    long totalExercises,
    double overallAccuracy,
    Map<String, TypeStats> byType,
    List<SessionSummary> recentSessions,
    List<WeakArea> weakestAreas
) {
    public record TypeStats(double accuracy, long totalAnswers) {}

    public record SessionSummary(Long sessionId, double accuracy, int totalExercises) {}

    /**
     * Padrão específico onde o utilizador tem pior desempenho.
     */
    public record WeakArea(
        String exerciseType,
        String pattern,
        String displayName,
        double accuracy,
        long totalAttempts,
        String hint
    ) {}
}
