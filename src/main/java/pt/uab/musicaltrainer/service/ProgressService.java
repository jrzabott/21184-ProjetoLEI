package pt.uab.musicaltrainer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.uab.musicaltrainer.api.ProgressResponse;
import pt.uab.musicaltrainer.dao.DaoFactory;
import pt.uab.musicaltrainer.dto.SessionRecord;

import java.util.ArrayList;
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

    public ProgressService(DaoFactory daoFactory, WeaknessHintProvider hintProvider) {
        this.daoFactory   = daoFactory;
        this.hintProvider = hintProvider;
    }

    public ProgressResponse buildProgress() throws Exception {
        logger.debug("Construindo dados de progresso");

        List<SessionRecord> sessions = daoFactory.createSessionDao().findAll();

        long totalSessions  = sessions.size();
        long totalExercises = sessions.stream().mapToLong(SessionRecord::totalExercises).sum();
        long totalCorrect   = sessions.stream().mapToLong(SessionRecord::correctAnswers).sum();
        double accuracy     = totalExercises == 0 ? 0.0 : (double) totalCorrect / totalExercises;

        // Agregação por tipo — dados reais da tabela results
        Map<String, long[]> typeCounts = daoFactory.createResultDao().countByExerciseType();
        Map<String, ProgressResponse.TypeStats> byType = typeCounts.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> new ProgressResponse.TypeStats(
                    e.getValue()[0] == 0 ? 0.0 : (double) e.getValue()[1] / e.getValue()[0],
                    e.getValue()[0]
                )
            ));

        List<ProgressResponse.SessionSummary> recent = sessions.stream()
            .limit(10)
            .map(s -> new ProgressResponse.SessionSummary(
                s.id(),
                s.totalExercises() == 0 ? 0.0 : (double) s.correctAnswers() / s.totalExercises(),
                s.totalExercises()
            ))
            .collect(Collectors.toList());

        // RF08 — padrões mais fracos com dicas pedagógicas
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
                int start = questionJson.indexOf('[') + 1;
                int comma = questionJson.indexOf(',', start);
                int end   = questionJson.indexOf(']', comma);
                int noteA = Integer.parseInt(questionJson.substring(start, comma).trim());
                int noteB = Integer.parseInt(questionJson.substring(comma + 1, end).trim());
                return pt.uab.musicaltrainer.domain.IntervalType
                    .fromSemitones(Math.abs(noteB - noteA)).internalName();
            } else {
                int typeStart = questionJson.indexOf("\"type\":\"") + 8;
                int typeEnd   = questionJson.indexOf('"', typeStart);
                return questionJson.substring(typeStart, typeEnd);
            }
        } catch (Exception e) {
            logger.warn("Erro a extrair padrão de: {}", questionJson);
            return "UNKNOWN";
        }
    }

    private String getDisplayName(String exerciseType, String pattern) {
        if ("INTERVAL".equals(exerciseType)) {
            return java.util.Arrays.stream(pt.uab.musicaltrainer.domain.IntervalType.values())
                .filter(t -> t.internalName().equals(pattern))
                .map(pt.uab.musicaltrainer.domain.IntervalType::displayName)
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
