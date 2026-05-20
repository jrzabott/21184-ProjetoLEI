package pt.uab.musicaltrainer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.api.ProgressResponse;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.domain.IntervalType;
import pt.uab.musicaltrainer.dto.ChordQuestion;
import pt.uab.musicaltrainer.dto.IntervalQuestion;
import pt.uab.musicaltrainer.dto.ScaleQuestion;
import pt.uab.musicaltrainer.dto.SessionRecord;
import pt.uab.musicaltrainer.generator.ExerciseType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Agrega dados de progresso para o dashboard.
 */
@Service
public class ProgressService {

    private static final Logger logger = LoggerFactory.getLogger(ProgressService.class);
    private final DaoFactory daoFactory;
    private final WeaknessHintProvider hintProvider;
    private final ObjectMapper objectMapper;

    public ProgressService(DaoFactory daoFactory, WeaknessHintProvider hintProvider,
                           ObjectMapper objectMapper) {
        this.daoFactory   = daoFactory;
        this.hintProvider = hintProvider;
        this.objectMapper = objectMapper;
    }

    public ProgressResponse buildProgress() throws Exception {
        logger.debug("Construindo dados de progresso");

        List<SessionRecord> sessions = daoFactory.createSessionDao().findAll();

        long totalSessions  = sessions.size();
        long totalExercises = sessions.stream().mapToLong(SessionRecord::totalExercises).sum();
        long totalCorrect   = sessions.stream().mapToLong(SessionRecord::correctAnswers).sum();
        double accuracy     = totalExercises == 0 ? 0.0 : (double) totalCorrect / totalExercises;

        // Zero-fill para todos os tipos - frontend não fica sem chave
        Map<String, long[]> typeCounts = daoFactory.createResultDao().countByExerciseType();
        Map<String, ProgressResponse.TypeStats> byType = new HashMap<>();
        for (ExerciseType type : ExerciseType.values()) {
            long[] counts = typeCounts.getOrDefault(type.name(), new long[]{0L, 0L});
            byType.put(type.name(), new ProgressResponse.TypeStats(
                counts[0] == 0 ? 0.0 : (double) counts[1] / counts[0],
                counts[0]
            ));
        }

        // Mais recentes primeiro, cap 100
        List<ProgressResponse.SessionSummary> recent = sessions.stream()
            .sorted(Comparator.comparing(SessionRecord::startTime).reversed())
            .limit(100)
            .map(s -> new ProgressResponse.SessionSummary(
                s.id(),
                s.startTime(),
                s.totalExercises() == 0 ? 0.0 : (double) s.correctAnswers() / s.totalExercises(),
                s.totalExercises()
            ))
            .collect(Collectors.toList());

        // RF08 - padrões mais fracos com dicas pedagógicas
        List<ProgressResponse.WeakArea> weakestAreas = new ArrayList<>();
        List<pt.uab.musicaltrainer.dao.ResultDao.WeaknessAggregate> aggregates =
            daoFactory.createResultDao().findWeaknessAggregates(3, 10);
        for (pt.uab.musicaltrainer.dao.ResultDao.WeaknessAggregate agg : aggregates) {
            String pattern     = extractPattern(agg.exerciseType(), agg.questionJson());
            String displayName = getDisplayName(agg.exerciseType(), pattern);
            double acc         = agg.total() == 0 ? 0.0 : (double) agg.correct() / agg.total();
            String hint        = hintProvider.getHint(agg.exerciseType(), pattern);
            weakestAreas.add(new ProgressResponse.WeakArea(
                agg.exerciseType(), pattern, displayName, acc, agg.total(), hint));
        }

        logger.info("Progresso: sessions={}, exercises={}, accuracy={}, tipos={}, fraquezas={}",
            totalSessions, totalExercises, accuracy, byType.keySet(), weakestAreas.size());

        return new ProgressResponse(totalSessions, totalExercises, accuracy, byType, recent, weakestAreas);
    }

    private String extractPattern(String exerciseType, String questionJson) {
        try {
            if ("INTERVAL".equals(exerciseType)) {
                IntervalQuestion q = objectMapper.readValue(questionJson, IntervalQuestion.class);
                int delta = Math.abs(q.notes()[1] - q.notes()[0]);
                int safeDelta = Math.min(delta, 12); // intervalos compostos > 12 semitones: clamp para OITAVA_PERFEITA
                return IntervalType.fromSemitones(safeDelta).internalName();
            } else if ("SCALE".equals(exerciseType)) {
                ScaleQuestion q = objectMapper.readValue(questionJson, ScaleQuestion.class);
                return q.type();
            } else {
                ChordQuestion q = objectMapper.readValue(questionJson, ChordQuestion.class);
                return q.type();
            }
        } catch (JsonProcessingException e) {
            logger.warn("Erro a parsear questionJson em extractPattern: {}", questionJson);
            return "UNKNOWN";
        }
    }

    private String getDisplayName(String exerciseType, String pattern) {
        if ("INTERVAL".equals(exerciseType)) {
            return java.util.Arrays.stream(IntervalType.values())
                .filter(t -> t.internalName().equals(pattern))
                .map(IntervalType::displayName)
                .findFirst().orElse(pattern);
        }
        // SCALE/CHORD: formatar nome enum para legível
        return pattern.replace("_", " ")
            .toLowerCase()
            .replace("harmonic", "Harmónica")
            .replace("natural", "Natural")
            .replace("major", "Maior")
            .replace("minor", "Menor")
            .replace("diminished", "Diminuto")
            .replace("augmented", "Aumentado");
    }
}
